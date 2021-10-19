package com.secondslot.coursework.features.chatscreen.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.secondslot.coursework.R
import com.secondslot.coursework.data.ReactionsSource
import com.secondslot.coursework.databinding.FragmentChatBinding
import com.secondslot.coursework.domain.model.ChatItem
import com.secondslot.coursework.domain.model.DateDivider
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.model.Reaction
import com.secondslot.coursework.extentions.getDateForChat
import com.secondslot.coursework.features.chatscreen.adapter.ChatAdapter
import com.secondslot.coursework.features.chatscreen.adapter.ReactionsAdapter
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment(), MessageInteractionListener, ChooseReactionListener {

//    private val viewModel by viewModels<ChatViewModel>()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val chatAdapter = ChatAdapter(this)
    private var chosenMessage: Message = Message()
    private var bottomSheetDialog: BottomSheetDialog? = null

    private var messages: ArrayList<ChatItem> = arrayListOf(
        Message(
            messageId = UUID.randomUUID(),
            userId = 1,
            datetime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 20,
            username = "Darrel Steward",
            userPhoto = "test_image.png",
            message = "Hi"
        ),
        Message(
            messageId = UUID.randomUUID(),
            userId = 1,
            datetime = System.currentTimeMillis() - 5000L,
            username = "Darrel Steward",
            message = "How are you?",
            userPhoto = "test_image.png",
            reactions = arrayListOf(
                Reaction(ReactionsSource().reactions[3].code, 1, isSelected = true),
            )
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        initViews()
        setListeners()

        return binding.root
    }

    private fun initViews() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        binding.recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = chatAdapter
        }

        updateMessages()
        binding.messageEditText.requestFocus()
    }

    private fun setListeners() {
        binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener { scrollToEnd() }

        binding.messageEditText.doAfterTextChanged { text ->
            setSendButtonAction(text.toString() == "")
        }

        binding.sendButton.setOnClickListener {
            if (binding.messageEditText.text.toString().isNotEmpty()) {
                messages.add(
                    Message(
                        messageId = UUID.randomUUID(),
                        datetime = System.currentTimeMillis(),
                        username = "Me",
                        message = binding.messageEditText.text.toString()
                    )
                )
                Log.d(TAG, "messages.size = ${messages.size}")

                binding.messageEditText.text.clear()

                updateMessages()
            } else {
                Log.d(TAG, "Add attachment clicked")
            }
        }
    }

    private fun updateMessages() {
        for (i in messages.size - 1 downTo 1) {
            if (messages[i - 1] is Message && messages[i] is Message) {
                if ((messages[i - 1] as Message).datetime.getDateForChat() !=
                    (messages[i] as Message).datetime.getDateForChat()
                ) {

                    messages.add(i, DateDivider((messages[i] as Message).datetime.getDateForChat()))
                }
            }
        }
        if (messages[0] !is DateDivider) {
            messages.add(0, DateDivider((messages[0] as Message).datetime.getDateForChat()))
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

    override fun openReactionsSheet(message: Message) {
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
            adapter = ReactionsAdapter(ReactionsSource().reactions, this@ChatFragment)
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

            val newMessage = Message(
                chosenMessage.messageId,
                chosenMessage.userId,
                chosenMessage.datetime,
                chosenMessage.username,
                chosenMessage.userPhoto,
                chosenMessage.message,
                ArrayList(chosenMessage.reactions)
            )


            messages[messages.indexOf(chosenMessage)] = newMessage

        } else if (!existingReaction.isSelected) {
            Log.d(TAG, "Existing reaction != null")

            existingReaction.isSelected = true
            existingReaction.count++

            val newMessage = Message(
                chosenMessage.messageId,
                chosenMessage.userId,
                chosenMessage.datetime,
                chosenMessage.username,
                chosenMessage.userPhoto,
                chosenMessage.message,
                ArrayList(chosenMessage.reactions)
            )

            messages[messages.indexOf(chosenMessage)] = newMessage
        }

        updateMessages()
    }

    override fun removeReaction(message: Message, reaction: Reaction) {
        val index = messages.indexOf(message)

        message.reactions.remove(reaction)

        val newMessage = Message(
            message.messageId,
            message.userId,
            message.datetime,
            message.username,
            message.userPhoto,
            message.message,
            ArrayList(message.reactions)
        )

        messages[index] = newMessage
        updateMessages()
    }

    companion object {
        private const val TAG = "ChatFragment"
    }
}
