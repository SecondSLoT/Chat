package com.secondslot.coursework.features.chat.ui

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.secondslot.coursework.R
import com.secondslot.coursework.extentions.fromHtml

class EditMessageDialog : DialogFragment() {

    private val requestKey by lazy { requireArguments().getString(API_REQUEST_KEY, "") }
    private val messageKey by lazy { requireArguments().getString(MESSAGE_KEY, "") }
    private val resultKey by lazy { requireArguments().getString(RESULT_KEY, "") }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_one_edit_text, null)
        val messageEditText = view.findViewById<EditText>(R.id.first_edit_text)
        messageEditText.setText(
            requireArguments().getString(OLD_MESSAGE_TEXT, "").fromHtml()
        )

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.edit_message))
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val newMessageText = messageEditText.text.toString()
                val resultBundle = bundleOf(
                    resultKey to Activity.RESULT_OK,
                    messageKey to newMessageText
                )
                setFragmentResult(requestKey, resultBundle)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                val resultBundle = bundleOf(resultKey to Activity.RESULT_CANCELED)
                setFragmentResult(requestKey, resultBundle)
                dialog.cancel()
            }
            .create()
    }

    companion object {
        private const val TAG = "CreateStreamDialog"
        private const val API_REQUEST_KEY = "api_request_key"
        private const val MESSAGE_KEY = "message_key"
        private const val OLD_MESSAGE_TEXT = "old_message_text"
        private const val RESULT_KEY = "result_key"

        fun newInstance(
            requestKey: String,
            messageKey: String,
            oldMessageText: String,
            resultKey: String
        ): DialogFragment {
            return EditMessageDialog().apply {
                arguments = bundleOf(
                    API_REQUEST_KEY to requestKey,
                    MESSAGE_KEY to messageKey,
                    OLD_MESSAGE_TEXT to oldMessageText,
                    RESULT_KEY to resultKey
                )
            }
        }
    }
}
