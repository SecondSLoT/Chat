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
import com.secondslot.coursework.data.db.ReactionsSource
import com.secondslot.coursework.databinding.FragmentChatBinding
import com.secondslot.coursework.domain.model.Reaction
import com.secondslot.coursework.domain.usecase.GetMessagesUseCase
import com.secondslot.coursework.extentions.getDateForChat
import com.secondslot.coursework.features.chat.adapter.ChatAdapter
import com.secondslot.coursework.features.chat.adapter.ReactionsAdapter
import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.model.DateDivider
import com.secondslot.coursework.features.chat.model.MessageItem
import com.secondslot.coursework.util.Temporary
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment(), MessageInteractionListener, ChooseReactionListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val chatAdapter = ChatAdapter(this)
    private var chosenMessage: MessageItem = MessageItem()
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val compositeDisposable = CompositeDisposable()

    private val getMessagesUseCase = GetMessagesUseCase()

    private var messages: List<ChatItem> = arrayListOf()

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
        binding.recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = chatAdapter
        }

        val topic = arguments?.getString(TOPIC, "") ?: ""
        binding.run {
            topicTextView.text = getString(R.string.topic, topic)
            messageEditText.requestFocus()
        }
    }

    private fun setListeners() {
        binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener { scrollToEnd() }

        binding.messageEditText.doAfterTextChanged { text ->
            setSendButtonAction(text.toString() == "")
        }

        binding.sendButton.setOnClickListener {
            if (binding.messageEditText.text.toString().isNotEmpty()) {
                if (Temporary.imitateError()) { // sending message error imitation
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.send_message_error),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    (messages as ArrayList).add(
                        MessageItem(
                            messageId = UUID.randomUUID(),
                            datetime = System.currentTimeMillis(),
                            username = "Me",
                            message = binding.messageEditText.text.toString()
                        )
                    )
                    binding.messageEditText.text.clear()

                    updateMessages()
                }

            } else {
                Log.d(TAG, "Add attachment clicked")
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setObservers() {
        getMessages(arguments?.getInt(CHANNEL_ID, 0) ?: 0)
    }

    private fun getMessages(channelId: Int) {
        val messagesObservable = getMessagesUseCase.execute(channelId)
        messagesObservable
            .subscribeOn(Schedulers.io())
            .map { MessageItem.fromDomainModel(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    messages = it
                    updateMessages()
                }
            )
            .also { compositeDisposable.add(it) }
    }

    private fun updateMessages() {
        for (i in messages.size - 1 downTo 1) {
            if (messages[i - 1] is MessageItem && messages[i] is MessageItem) {
                if ((messages[i - 1] as MessageItem).datetime.getDateForChat() !=
                    (messages[i] as MessageItem).datetime.getDateForChat()
                ) {

                    (messages as ArrayList).add(
                        i, DateDivider(
                            (messages[i] as MessageItem)
                                .datetime.getDateForChat()
                        )
                    )
                }
            }
        }
        if (messages[0] !is DateDivider) {
            (messages as ArrayList).add(
                0, DateDivider(
                    (messages[0] as MessageItem)
                        .datetime.getDateForChat()
                )
            )
        }
        chatAdapter.submitList(messages.toList())
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
        ?.takeIf { it > 0 }?.let(binding.recyclerView::smoothScrollToPosition)

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
            adapter = ReactionsAdapter(ReactionsSource.reactions, this@ChatFragment)
            setHasFixedSize(true)
        }
        bottomSheetDialog?.show()
    }

    override fun reactionChosen(reaction: Reaction) {
        bottomSheetDialog?.dismiss()

        val existingReaction = chosenMessage.reactions.find { it.code == reaction.code }

        if (existingReaction == null) {
            Log.d(TAG, "Existing reaction == null")
            reaction.isSelected = true
            chosenMessage.reactions.add(reaction)

            val newMessage = MessageItem(
                chosenMessage.messageId,
                chosenMessage.userId,
                chosenMessage.datetime,
                chosenMessage.username,
                chosenMessage.userPhoto,
                chosenMessage.message,
                ArrayList(chosenMessage.reactions)
            )


            (messages as ArrayList)[messages.indexOf(chosenMessage)] = newMessage

        } else if (!existingReaction.isSelected) {
            Log.d(TAG, "Existing reaction != null")

            existingReaction.isSelected = true
            existingReaction.count++

            val newMessage = MessageItem(
                chosenMessage.messageId,
                chosenMessage.userId,
                chosenMessage.datetime,
                chosenMessage.username,
                chosenMessage.userPhoto,
                chosenMessage.message,
                ArrayList(chosenMessage.reactions)
            )

            (messages as ArrayList)[messages.indexOf(chosenMessage)] = newMessage
        }

        updateMessages()
    }

    override fun removeReaction(message: MessageItem, reaction: Reaction) {
        val index = messages.indexOf(message)

        message.reactions.remove(reaction)

        val newMessage = MessageItem(
            message.messageId,
            message.userId,
            message.datetime,
            message.username,
            message.userPhoto,
            message.message,
            ArrayList(message.reactions)
        )

        (messages as ArrayList)[index] = newMessage
        updateMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        private const val TAG = "ChatFragment"
        private const val CHANNEL_ID = "channel_id"
        private const val TOPIC = "topic" // temporary

        fun newInstance(channelId: Int, topic: String): ChatFragment {
            return ChatFragment().apply {
                arguments = bundleOf(
                    CHANNEL_ID to channelId,
                    TOPIC to topic
                )
            }
        }
    }
}
