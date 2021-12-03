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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.secondslot.coursework.App
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentChatBinding
import com.secondslot.coursework.features.chat.adapter.ChatAdapter
import com.secondslot.coursework.features.chat.adapter.ReactionsAdapter
import com.secondslot.coursework.features.chat.di.DaggerChatComponent
import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.vm.ChatViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _viewModel: ChatViewModel? = null
    private val viewModel get() = requireNotNull(_viewModel)

    private var _binding: FragmentChatBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var chatAdapter: ChatAdapter? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

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

        _viewModel = ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]

        viewModel.streamId = arguments?.getInt(STREAM_ID, 0) ?: 0
        viewModel.topicName = arguments?.getString(TOPIC_NAME, "") ?: ""

        initViews()
        setListeners()
        setObservers()
        return binding.root
    }

    private fun initViews() {
        lifecycleScope.launch {
            viewModel.getMyId().collect { myId ->
                val linearLayoutManager = LinearLayoutManager(requireContext())
                linearLayoutManager.stackFromEnd = true

                chatAdapter = ChatAdapter(viewModel as MessageInteractionListener, myId)

                binding.recyclerView.run {
                    layoutManager = linearLayoutManager
                    adapter = chatAdapter
                }

                binding.run {
                    toolbar.title = viewModel.getStreamName()
                    topicTextView.text = getString(R.string.topic, viewModel.topicName)
                    messageEditText.requestFocus()
                }

                viewModel.getMessages(isScrollToEnd = true)
            }
        }
    }

    private fun setListeners() {

        binding.messageEditText.doAfterTextChanged { text ->
            setSendButtonAction(text.toString() == "")
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    viewModel.onScrollUp(
                        (binding.recyclerView.layoutManager as LinearLayoutManager)
                            .findFirstCompletelyVisibleItemPosition()
                    )
                } else {
                    viewModel.onScrollDown(
                        (binding.recyclerView.layoutManager as LinearLayoutManager)
                            .findLastCompletelyVisibleItemPosition()
                    )
                }
            }
        })

        binding.sendButton.setOnClickListener {
            if (binding.messageEditText.text.toString().isNotEmpty()) {
                viewModel.onSendMessageClicked(binding.messageEditText.text.toString())
            } else {
                Log.d(TAG, "Add attachment clicked")
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setObservers() {
        lifecycleScope.run {

            var updateMessagesJustLaunched = true
            launchWhenStarted {
                viewModel.updateMessagesFlow.collect { updateEvent ->
                    if (updateMessagesJustLaunched) {
                        updateMessagesJustLaunched = false
                    } else {
                        updateEvent.getContentIfNotHandled()?.let {
                            updateMessages(it.first, it.second)
                        }
                    }
                }
            }

            var showErrorJustLaunched = true
            launchWhenStarted {
                viewModel.showErrorFlow.collect { isShowErrorEvent ->
                    if (showErrorJustLaunched) {
                        showErrorJustLaunched = false
                    } else {
                        isShowErrorEvent.getContentIfNotHandled()?.let { error ->
                            showError(error)
                        }
                    }
                }
            }

            var showSendErrorJustLaunched = true
            launchWhenStarted {
                viewModel.showSendMessageErrorFlow.collect { isShowErrorEvent ->
                    if (showSendErrorJustLaunched) {
                        showSendErrorJustLaunched = false
                    } else {
                        isShowErrorEvent.getContentIfNotHandled()?.let { error ->
                            showSendMessageError(error)
                        }
                    }
                }
            }

            launchWhenStarted {
                viewModel.clearMessageFlow.collect { isClearEvent ->
                    isClearEvent.getContentIfNotHandled()?.let { isClearMessageEditText ->
                        if (isClearMessageEditText) clearMessageEditText()
                    }
                }
            }

            launchWhenStarted {
                viewModel.openReactionsSheet.collect { isOpenEvent ->
                    isOpenEvent.getContentIfNotHandled()?.let { isOpen ->
                        if (isOpen) openReactionsSheet()
                    }
                }
            }

            launchWhenStarted {
                viewModel.closeReactionsSheet.collect { isCloseEvent ->
                    isCloseEvent.getContentIfNotHandled()?.let { isClose ->
                        if (isClose) closeReactionsSheet()
                    }
                }
            }
        }
    }

    private fun updateMessages(messages: List<ChatItem>, isScrollToEnd: Boolean) {
        chatAdapter?.submitList(messages.toList()) {
            if (isScrollToEnd) scrollToEnd()
        } ?: Log.e(TAG, "chatAdapter is null")
    }

    private fun showSendMessageError(error: Throwable?) {
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

    private fun showError(error: Throwable?) {
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

    private fun clearMessageEditText() {
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

    private fun openReactionsSheet() {

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
                viewModel.getReactions(),
                viewModel as ChooseReactionListener
            )
            setHasFixedSize(true)
        }
        bottomSheetDialog?.show()
    }

    private fun closeReactionsSheet() {
        bottomSheetDialog?.dismiss()
    }

    companion object {
        private const val TAG = "ChatFragment"
        const val TOPIC_NAME = "topic_name"
        const val MAX_MESSAGE_ID = "max_message_id"
        const val STREAM_ID = "stream_id"

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
