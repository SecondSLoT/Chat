package com.secondslot.coursework.features.chat.ui

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.secondslot.coursework.R

class MoveMessageDialog : DialogFragment() {

    private val requestKey by lazy { requireArguments().getString(API_REQUEST_KEY, "") }
    private val newTopicKey by lazy { requireArguments().getString(NEW_TOPIC_KEY, "") }
    private val resultKey by lazy { requireArguments().getString(RESULT_KEY, "") }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_one_edit_text, null)
        val topicEditText = view.findViewById<AutoCompleteTextView>(R.id.first_edit_text)
        val topicsList = requireArguments().getStringArrayList(TOPICS) ?: emptyList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            topicsList
        )

        Log.d(TAG, "topicsList = ${topicsList.joinToString()}")

        topicEditText.run {
            hint = getString(R.string.topic_hint)
            threshold = 1
            setAdapter(adapter)

            onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                this.setText(parent.getItemAtPosition(position).toString())
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.move_to_topic))
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val newTopic = topicEditText.text.toString()
                val resultBundle = bundleOf(
                    resultKey to Activity.RESULT_OK,
                    newTopicKey to newTopic
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
        private const val TAG = "MoveMessageDialog"
        private const val API_REQUEST_KEY = "api_request_key"
        private const val TOPICS = "topics_key"
        private const val NEW_TOPIC_KEY = "new_topic_key"
        private const val RESULT_KEY = "result_key"

        fun newInstance(
            requestKey: String,
            topics: List<String>,
            newTopicKey: String,
            resultKey: String
        ): DialogFragment {
            return MoveMessageDialog().apply {
                arguments = bundleOf(
                    API_REQUEST_KEY to requestKey,
                    TOPICS to topics,
                    NEW_TOPIC_KEY to newTopicKey,
                    RESULT_KEY to resultKey
                )
            }
        }
    }
}
