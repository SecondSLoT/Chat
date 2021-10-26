package com.secondslot.coursework.features.channels.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.R
import com.secondslot.coursework.data.ChannelsSource
import com.secondslot.coursework.features.channels.adapter.ChannelsListAdapter
import com.secondslot.coursework.features.channels.adapter.DividerItemDecoration

class ChannelsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_channels_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val contentList = when (arguments?.getString(CONTENT_KEY, "")) {
            SUBSCRIBED -> ChannelsSource.subscribed
            else -> ChannelsSource.allStreams
        }

        val channelsListAdapter = ChannelsListAdapter(contentList)

        recyclerView.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = channelsListAdapter
            addItemDecoration(DividerItemDecoration())
        }
    }

    companion object {
        private const val CONTENT_KEY = "list_type"
        const val SUBSCRIBED = "subscribed"
        const val ALL_STREAMS = "all_streams"

        fun newInstance(contentKey: String): Fragment {
            return ChannelsListFragment().apply {
                arguments = bundleOf(CONTENT_KEY to contentKey)
            }
        }
    }
}
