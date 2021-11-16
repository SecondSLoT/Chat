package com.secondslot.coursework.features.chat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.secondslot.coursework.R
import com.secondslot.coursework.data.local.ReactionStorage
import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.databinding.FragmentChatBinding
import com.secondslot.coursework.domain.model.Reaction
import com.secondslot.coursework.domain.usecase.AddReactionUseCase
import com.secondslot.coursework.domain.usecase.GetMessagesUseCase
import com.secondslot.coursework.domain.usecase.GetOwnProfileUseCase
import com.secondslot.coursework.domain.usecase.RemoveReactionUseCase
import com.secondslot.coursework.domain.usecase.SendMessageUseCase
import com.secondslot.coursework.extentions.getDateForChat
import com.secondslot.coursework.features.chat.adapter.ChatAdapter
import com.secondslot.coursework.features.chat.adapter.ReactionsAdapter
import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.model.DateDivider
import com.secondslot.coursework.features.chat.model.MessageItem
import com.secondslot.coursework.features.chat.model.MessageToItemMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment(), MessageInteractionListener, ChooseReactionListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var myId: Int = -1
    private var chatAdapter: ChatAdapter? = null
    private var chosenMessage: MessageItem? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val compositeDisposable = CompositeDisposable()

    private val getMessagesUseCase = GetMessagesUseCase()
    private val sendMessageUseCase = SendMessageUseCase()
    private val getOwnProfileUseCase = GetOwnProfileUseCase()
    private val addReactionUseCase = AddReactionUseCase()
    private val removeReactionUseCase = RemoveReactionUseCase()

    private var messages: List<ChatItem> = arrayListOf()
    private var firstMessageId: Int = -1
    private var isLoading: Boolean = false
        set(value) {
            field = value
            Log.d(TAG, "isLoading = $field")
        }

    private val topicName: String by lazy { arguments?.getString(TOPIC_NAME, "") ?: "" }
    private val streamId: Int by lazy { arguments?.getInt(STREAM_ID, 0) ?: 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.username)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        initViews()
        setObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun initViews() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        binding.recyclerView.layoutManager = linearLayoutManager

        // Get own profile for my ID to set it to chatAdapter
        getOwnProfileUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { user ->
                    if (user.isNotEmpty()) {
                        myId = user[0].userId
                        chatAdapter = ChatAdapter(this, myId)
                        binding.recyclerView.adapter = chatAdapter
                    }
                },
                onError = {
                    Log.d(TAG, "getOwnProfileUseCase.execute() error")
                    showError(it)
                }
            )
            .addTo(compositeDisposable)

        binding.run {
            topicTextView.text = getString(R.string.topic, topicName)
            messageEditText.requestFocus()
        }
    }

    private fun setListeners() {
        binding.messageEditText.doAfterTextChanged { text ->
            setSendButtonAction(text.toString() == "")
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // If user scrolls up
                if (dy < 0 && !isLoading) {
                    // If item on position to prefetch is visible
                    if (
                        (binding.recyclerView.layoutManager as LinearLayoutManager)
                            .findFirstCompletelyVisibleItemPosition() == PREFETCH_DISTANCE
                    ) {

                        Log.d(TAG, "It's time to prefetch")
                        getMessages(firstMessageId.toString(), false)
                    }
                }
            }
        })

        binding.sendButton.setOnClickListener {
            if (binding.messageEditText.text.toString().isNotEmpty()) {
                sendMessage(binding.messageEditText.text.toString())
            } else {
                Log.d(TAG, "Add attachment clicked")
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setObservers() {
        getMessages(isScrollToEnd = true)
    }

    private fun getMessages(anchor: String = "newest", isScrollToEnd: Boolean = false) {
        if (isLoading) return
        isLoading = true

        // firstMessageId != -1 means that we already have some loaded messages and
        // now we need to load a new batch of messages
        if (firstMessageId != -1) {
            // Remove item at index 0 - time divider
            // Remove item at index 1 - first message, because it will be at the next batch
            (messages as ArrayList).subList(0, 2).clear()
        }

        val narrow = mapOf(
            "stream" to streamId,
            "topic" to topicName
        )

        Log.d(TAG, "GetMessagesUseCase execute")
        getMessagesUseCase.execute(anchor = anchor, narrow = narrow)
            .subscribeOn(Schedulers.io())
            .map { MessageToItemMapper.map(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Log.d(TAG, "MessageObservable onNext")
                    if (it.isNotEmpty()) {
                        firstMessageId = it[0].id
                        (messages as ArrayList).addAll(0, it)
                        updateMessages(isScrollToEnd)
                    }
                },
                onError = {
                    showError(it)
                    isLoading = false
                },
                onComplete = {
                    isLoading = false
                }
            )
            .also { compositeDisposable.add(it) }
    }

    private fun sendMessage(messageText: String) {
        val sendMessageResultSingle = sendMessageUseCase.execute(
            streamId = streamId,
            topicName = topicName,
            messageText = messageText
        )
        sendMessageResultSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    if (it.result == SERVER_RESULT_SUCCESS) {
                        binding.messageEditText.text.clear()
                        getMessages(isScrollToEnd = true)
                    } else {
                        showSendMessageError()
                    }
                },
                onError = { showSendMessageError(it) }
            )
            .addTo(compositeDisposable)
    }

    private fun showSendMessageError(error: Throwable? = null) {
        Toast.makeText(
            requireContext(),
            R.string.send_message_error,
            Toast.LENGTH_SHORT
        ).show()
        if (error == null) {
            Log.e(TAG, getString(R.string.send_message_error))
        } else {
            Log.e(TAG, "${getString(R.string.send_message_error)}: $error")
        }
    }

    private fun showError(error: Throwable? = null) {
        Toast.makeText(
            requireContext(),
            R.string.network_error_message,
            Toast.LENGTH_SHORT
        ).show()
        if (error == null) {
            Log.e(TAG, getString(R.string.network_error_message))
        } else {
            Log.e(TAG, "${getString(R.string.network_error_message)} $error")
        }
    }

    private fun updateMessages(isScrollToEnd: Boolean = false) {
        for (i in messages.size - 1 downTo 1) {
            if (messages[i - 1] is MessageItem && messages[i] is MessageItem) {
                if ((messages[i - 1] as MessageItem).timestamp.getDateForChat() !=
                    (messages[i] as MessageItem).timestamp.getDateForChat()
                ) {

                    (messages as ArrayList).add(
                        i, DateDivider(
                            (messages[i] as MessageItem)
                                .timestamp.getDateForChat()
                        )
                    )
                }
            }
        }

        if (messages[0] !is DateDivider) {
            (messages as ArrayList).add(
                0, DateDivider(
                    (messages[0] as MessageItem)
                        .timestamp.getDateForChat()
                )
            )
        }
        chatAdapter?.submitList(messages.toList()) {
            if (isScrollToEnd) scrollToEnd()
        }
    }

    private fun setSendButtonAction(isMessageEmpty: Boolean) {
        if (isMessageEmpty) {
            binding.sendButton.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_add_24)
            )
        } else {
            binding.sendButton.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_send_24)
            )
        }
    }

    private fun scrollToEnd() = binding.recyclerView.adapter?.itemCount?.minus(1)
        ?.takeIf { it > 0 }?.let(binding.recyclerView::scrollToPosition)

    override fun openReactionsSheet(message: MessageItem) {
        chosenMessage = message
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.run {
            setContentView(R.layout.dialog_reactions_bottom_sheet)
            setCancelable(false)
            setCanceledOnTouchOutside(true)
        }

        val reactionsRecyclerView =
            bottomSheetDialog?.findViewById<RecyclerView>(R.id.reactions_recycler_view)
        reactionsRecyclerView?.run {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = ReactionsAdapter(ReactionStorage.reactions, this@ChatFragment)
            setHasFixedSize(true)
        }
        bottomSheetDialog?.show()
    }

    override fun reactionChosen(reaction: ReactionLocal) {
        bottomSheetDialog?.dismiss()

        var existingReaction: Reaction? = null
        chosenMessage?.reactions?.forEach {
            if (it.key.emojiCode == reaction.emojiCode && it.key.userId == myId) {
                existingReaction = it.key
                return
            }
        }

        if (existingReaction == null) {
            chosenMessage?.id?.let { addReaction(it, reaction.emojiName) }
        } else {
            chosenMessage?.id?.let { removeReaction(it, reaction.emojiName) }
        }
    }

    override fun addReaction(messageId: Int, emojiName: String) {
        val addReactionResult = addReactionUseCase.execute(messageId, emojiName)

        addReactionResult
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    if (it.result == SERVER_RESULT_SUCCESS) {
                        getMessages()
                    } else {
                        showError()
                        Log.e(TAG, "Error adding reaction = ${it.result}")
                    }
                },
                onError = {
                    showError()
                    Log.e(TAG, "Error adding reaction = ${it.message}")
                }
            )
            .addTo(compositeDisposable)
    }

    override fun removeReaction(messageId: Int, emojiName: String) {
        val removeReactionResult = removeReactionUseCase.execute(messageId, emojiName)

        removeReactionResult
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    if (it.result == SERVER_RESULT_SUCCESS) {
                        getMessages()
                    } else {
                        showError()
                        Log.e(TAG, "Error removing reaction = ${it.result}")
                    }
                },
                onError = {
                    showError()
                    Log.e(TAG, "Error removing reaction = ${it.message}")
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        private const val TAG = "ChatFragment"
        private const val TOPIC_NAME = "topic_name"
        private const val MAX_MESSAGE_ID = "max_message_id"
        private const val STREAM_ID = "stream_id"

        private const val SERVER_RESULT_SUCCESS = "success"
        private const val SERVER_RESULT_ERROR = "error"

        private const val PREFETCH_DISTANCE = 4

        fun newInstance(topicName: String, maxMessageId: Int, streamId: Int): ChatFragment {
            return ChatFragment().apply {
                arguments = bundleOf(
                    TOPIC_NAME to topicName,
                    MAX_MESSAGE_ID to maxMessageId,
                    STREAM_ID to streamId
                )
            }
        }
    }
}
