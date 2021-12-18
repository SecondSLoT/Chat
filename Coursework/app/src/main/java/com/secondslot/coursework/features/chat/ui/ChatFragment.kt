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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.secondslot.coursework.App
import com.secondslot.coursework.R
import com.secondslot.coursework.base.mvp.MvpFragment
import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.databinding.FragmentChatBinding
import com.secondslot.coursework.di.NavigatorFactory
import com.secondslot.coursework.features.chat.adapter.ChatAdapter
import com.secondslot.coursework.features.chat.adapter.MessageMenuAdapter
import com.secondslot.coursework.features.chat.adapter.ReactionsAdapter
import com.secondslot.coursework.features.chat.di.ChatPresenterFactory
import com.secondslot.coursework.features.chat.di.DaggerChatComponent
import com.secondslot.coursework.features.chat.listener.ChooseReactionListener
import com.secondslot.coursework.features.chat.listener.MessageInteractionListener
import com.secondslot.coursework.features.chat.listener.MessageMenuListener
import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.model.MessageItem
import com.secondslot.coursework.features.chat.model.MessageMenuItem
import com.secondslot.coursework.features.chat.presenter.ChatPresenter
import com.secondslot.coursework.navigation.AppNavigation
import javax.inject.Inject

class ChatFragment :
    MvpFragment<ChatView, ChatPresenter>(),
    ChatView,
    MessageInteractionListener,
    MessageMenuListener,
    ChooseReactionListener {

    @Inject
    internal lateinit var presenterFactory: ChatPresenterFactory

    internal lateinit var presenter: ChatPresenter

    @Inject
    internal lateinit var navigationFactory: NavigatorFactory

    private lateinit var navigator: AppNavigation

    private var _binding: FragmentChatBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var chatAdapter: ChatAdapter? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetMessageMenu: BottomSheetDialog? = null

    override fun getPresenter(): ChatPresenter = presenter

    override fun getMvpView(): ChatView = this

    private fun getStreamId(): Int {
        return arguments?.getInt(STREAM_ID, 0) ?: 0
    }

    private fun getTopicName(): String {
        return arguments?.getString(TOPIC_NAME, "") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatComponent = DaggerChatComponent.factory().create(App.appComponent)
        chatComponent.inject(this)
        presenter = presenterFactory.create(getStreamId(), getTopicName())
        navigator = navigationFactory.create(requireActivity())

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.username)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        setListeners()
        return binding.root
    }

    override fun initViews(myId: Int) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true

        chatAdapter = ChatAdapter(this, myId)

        binding.recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = chatAdapter
        }

        binding.run {
            topicTextView.text = getString(R.string.topic, getTopicName())
            messageEditText.requestFocus()
        }

        presenter.loadMessages(
            anchor = "newest",
            isLoadNew = false,
            isScrollToEnd = true
        )
    }

    override fun showStreamName(streamName: String) {
        binding.toolbar.title = "#${streamName}"
    }

    private fun setListeners() {

        binding.messageEditText.doAfterTextChanged { text ->
            setSendButtonAction(text.toString() == "")
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    presenter.onScrollUp(
                        (binding.recyclerView.layoutManager as LinearLayoutManager)
                            .findFirstCompletelyVisibleItemPosition()
                    )
                } else {
                    presenter.onScrollDown(
                        (binding.recyclerView.layoutManager as LinearLayoutManager)
                            .findLastCompletelyVisibleItemPosition()
                    )
                }
            }
        })

        binding.sendButton.setOnClickListener {
            if (binding.messageEditText.text.toString().isNotEmpty()) {
                presenter.onSendMessageClicked(binding.messageEditText.text.toString())
            } else {
                Log.d(TAG, "Add attachment clicked")
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun updateMessages(messages: List<ChatItem>, isScrollToEnd: Boolean) {
        chatAdapter?.submitList(messages.toList()) {
            if (isScrollToEnd) scrollToEnd()
        } ?: Log.e(TAG, "chatAdapter is null")
    }

    override fun showSendMessageError(error: Throwable?) {
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

    override fun showError(error: Throwable?) {
        Toast.makeText(
            requireContext(),
            R.string.error_message,
            Toast.LENGTH_SHORT
        ).show()
        if (error == null) {
            Log.e(TAG, getString(R.string.error_message))
        } else {
            Log.e(TAG, "${getString(R.string.error_message)} $error")
        }
    }

    override fun clearMessageEditText() {
        binding.messageEditText.text.clear()
    }

    private fun setSendButtonAction(isMessageEmpty: Boolean) {
        if (isMessageEmpty) {
            binding.sendButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_attach_file_24
                )
            )
        } else {
            binding.sendButton.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_send_24)
            )
        }
    }

    private fun scrollToEnd() = binding.recyclerView.adapter?.itemCount?.minus(1)
        ?.takeIf { it > 0 }?.let(binding.recyclerView::scrollToPosition)

    override fun openReactionsSheet() {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.run {
            setContentView(R.layout.dialog_bottom_sheet)
            setCancelable(false)
            setCanceledOnTouchOutside(true)
        }

        val reactionsRecyclerView =
            bottomSheetDialog?.findViewById<RecyclerView>(R.id.recycler_view)
        reactionsRecyclerView?.run {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = ReactionsAdapter(
                presenter.getReactions(),
                this@ChatFragment
            )
            setHasFixedSize(true)
        }
        bottomSheetDialog?.show()
    }

    override fun closeReactionsSheet() {
        bottomSheetDialog?.dismiss()
    }

    override fun openMessageMenu(menuList: List<MessageMenuItem>) {
        bottomSheetMessageMenu = BottomSheetDialog(requireContext())
        bottomSheetMessageMenu?.run {
            setContentView(R.layout.dialog_bottom_sheet)
            setCancelable(false)
            setCanceledOnTouchOutside(true)
        }

        val messageMenuRecyclerView =
            bottomSheetMessageMenu?.findViewById<RecyclerView>(R.id.recycler_view)
        messageMenuRecyclerView?.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MessageMenuAdapter(menuList, this@ChatFragment)
            setHasFixedSize(true)
        }
        bottomSheetMessageMenu?.show()
    }

    override fun closeMessageMenu() {
        bottomSheetMessageMenu?.dismiss()
    }

    override fun reactionChosen(reaction: ReactionLocal) {
        presenter.onReactionChosen(reaction)
    }

    override fun messageOnLongClick(message: MessageItem) {
        presenter.onMessageLongClick(message)
    }

    override fun onAddReactionButtonClick(message: MessageItem) {
        presenter.onAddReactionButtonClicked(message)
    }

    override fun addReaction(messageId: Int, emojiName: String) {
        presenter.onAddReaction(messageId, emojiName)
    }

    override fun removeReaction(messageId: Int, emojiName: String) {
        presenter.onRemoveReaction(messageId, emojiName)
    }

    override fun onMessageMenuItemClick(itemId: Int) {
        presenter.onMessageMenuItemClick(itemId)
    }

    override fun notifyMessageMoved(topicName: String) {
        val text = getString(R.string.notify_message_moved, topicName)
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun notifySameTopic() {
        Toast.makeText(requireContext(), R.string.notify_same_topic, Toast.LENGTH_SHORT).show()
    }

    override fun openEditMessageDialog(curMessageText: String) {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            EDIT_MESSAGE_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getInt(RESULT_KEY, -1)
            val newMessageText = bundle.getString(EDITED_MESSAGE_KEY, "")
            presenter.onEditMessage(result, newMessageText)
        }

        navigator.navigateToEditMessageDialog(
            requestKey = EDIT_MESSAGE_REQUEST_KEY,
            messageKey = EDITED_MESSAGE_KEY,
            oldMessageText = curMessageText,
            resultKey = RESULT_KEY
        )
    }

    override fun openDeleteMessageDialog() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            DELETE_MESSAGE_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getInt(RESULT_KEY, -1)
            presenter.onDeleteMessage(result)
        }

        navigator.navigateToStandardAlertDialog(
            requestKey = DELETE_MESSAGE_REQUEST_KEY,
            title = getString(R.string.delete_message),
            message = getString(R.string.are_you_sure),
            positiveButtonText = getString(R.string.delete),
            resultKey = RESULT_KEY
        )
    }

    override fun openMoveMessageDialog(topics: List<String>) {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            MOVE_MESSAGE_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getInt(RESULT_KEY, -1)
            val newTopic = bundle.getString(NEW_TOPIC_KEY, "")
            presenter.onMoveMessage(result, newTopic)
        }

        navigator.navigateToMoveMessageDialog(
            requestKey = MOVE_MESSAGE_REQUEST_KEY,
            topics = topics,
            newTopicKey = NEW_TOPIC_KEY,
            resultKey = RESULT_KEY
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ChatFragment"
        private const val TOPIC_NAME = "topic_name"
        private const val MAX_MESSAGE_ID = "max_message_id"
        private const val STREAM_ID = "stream_id"

        private const val EDIT_MESSAGE_REQUEST_KEY = "edit_message_request_key"
        private const val EDITED_MESSAGE_KEY = "edited_message_text"
        private const val DELETE_MESSAGE_REQUEST_KEY = "delete_message_request_key"
        private const val MOVE_MESSAGE_REQUEST_KEY = "move_message_request_key"
        private const val NEW_TOPIC_KEY = "new_topic_key"
        private const val RESULT_KEY = "result_key"

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
