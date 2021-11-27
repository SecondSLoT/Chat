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
import com.secondslot.coursework.databinding.FragmentChatBinding
import com.secondslot.coursework.di.GlobalDI
import com.secondslot.coursework.features.chat.adapter.ChatAdapter
import com.secondslot.coursework.features.chat.adapter.ReactionsAdapter
import com.secondslot.coursework.features.chat.di.DaggerChatComponent
import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.presenter.ChatContract
import javax.inject.Inject

class ChatFragment :
    MvpFragment<ChatContract.ChatView, ChatContract.ChatPresenter>(),
    ChatContract.ChatView {

    @Inject
    internal lateinit var presenter: ChatContract.ChatPresenter

    private var _binding: FragmentChatBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var chatAdapter: ChatAdapter? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun getPresenter(): ChatContract.ChatPresenter = presenter

    override fun getMvpView(): ChatContract.ChatView = this

    override fun getStreamId(): Int {
        return arguments?.getInt(STREAM_ID, 0) ?: 0
    }

    override fun getTopicName(): String {
        return arguments?.getString(TOPIC_NAME, "") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatComponent = DaggerChatComponent.factory().create(App.appComponent)
        chatComponent.inject(this)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.username)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    override fun initViews(myId: Int) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true

        chatAdapter = ChatAdapter(presenter as MessageInteractionListener, myId)

        binding.recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = chatAdapter
        }

        binding.run {
            topicTextView.text = getString(R.string.topic, presenter.getTopicName())
            messageEditText.requestFocus()
        }

        presenter.getMessages(isScrollToEnd = true)
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

    override fun openReactionsSheet() {

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
            adapter = ReactionsAdapter(
                presenter.getReactions(),
                presenter as ChooseReactionListener)
            setHasFixedSize(true)
        }
        bottomSheetDialog?.show()
    }

    override fun closeReactionsSheet() {
        bottomSheetDialog?.dismiss()
    }

    companion object {
        private const val TAG = "ChatFragment"
        private const val TOPIC_NAME = "topic_name"
        private const val MAX_MESSAGE_ID = "max_message_id"
        private const val STREAM_ID = "stream_id"

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
