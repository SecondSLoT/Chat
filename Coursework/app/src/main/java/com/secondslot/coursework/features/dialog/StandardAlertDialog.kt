package com.secondslot.coursework.features.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class StandardAlertDialog : DialogFragment() {

    private val requestKey by lazy { requireArguments().getString(API_REQUEST_KEY, "") }
    private val resultKey by lazy { requireArguments().getString(RESULT_KEY, "") }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireArguments().getString(TITLE, ""))
            .setMessage(requireArguments().getString(MESSAGE, ""))
            .setPositiveButton(
                requireArguments().getString(POSITIVE_BUTTON_TEXT, "")
            ) { _, _ ->
                val resultBundle = bundleOf(resultKey to Activity.RESULT_OK)
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
        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val POSITIVE_BUTTON_TEXT = "positive_button_text"
        private const val RESULT_KEY = "result_key"

        fun newInstance(
            requestKey: String,
            title: String,
            message: String,
            positiveButtonText: String,
            resultKey: String
        ): DialogFragment {
            return StandardAlertDialog().apply {
                arguments = bundleOf(
                    API_REQUEST_KEY to requestKey,
                    TITLE to title,
                    MESSAGE to message,
                    POSITIVE_BUTTON_TEXT to positiveButtonText,
                    RESULT_KEY to resultKey
                )
            }
        }
    }
}
