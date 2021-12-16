package com.secondslot.coursework.features.channels.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.secondslot.coursework.R

class CreateStreamDialog : DialogFragment() {

    private val requestKey by lazy { requireArguments().getString(API_REQUEST_KEY, "") }
    private val streamNameKey by lazy {
        requireArguments().getString(STREAM_NAME_KEY, "")
    }
    private val descriptionKey by lazy {
        requireArguments().getString(DESCRIPTION_KEY, "")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_two_edit_texts, null)
        val streamNameEditText = view.findViewById<EditText>(R.id.first_edit_text)
        val descriptionEditText = view.findViewById<EditText>(R.id.second_edit_text)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.fab_content_description))
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                Log.d(TAG, "OK clicked")
                if (streamNameEditText.text.toString().isNotEmpty()) {
                    val streamName = streamNameEditText.text.toString()
                    val description = descriptionEditText.text.toString()
                    val resultBundle = bundleOf(
                        streamNameKey to streamName,
                        descriptionKey to description
                    )
                    Log.d(TAG, "setFragmentResult")
                    setFragmentResult(requestKey, resultBundle)
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                Log.d(TAG, "Cancel clicked")
                dialog.cancel()
            }
            .create()
    }

    companion object {
        private const val TAG = "CreateStreamDialog"
        private const val API_REQUEST_KEY = "api_request_key"
        private const val STREAM_NAME_KEY = "stream_name"
        private const val DESCRIPTION_KEY = "description"

        fun newInstance(
            requestKey: String,
            streamNameKey: String,
            descriptionKey: String
        ): DialogFragment {
            return CreateStreamDialog().apply {
                arguments = bundleOf(
                    API_REQUEST_KEY to requestKey,
                    STREAM_NAME_KEY to streamNameKey,
                    DESCRIPTION_KEY to descriptionKey
                )
            }
        }
    }
}
