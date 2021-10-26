package com.secondslot.coursework.features.channels.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentChannelsBinding
import com.secondslot.coursework.features.channels.adapter.ChannelsPagerAdapter

class ChannelsFragment : Fragment() {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        val tabs: List<String> = listOf(
            getString(R.string.subscribed),
            getString(R.string.all_streams)
        )

        val channelsPagerAdapter: ChannelsPagerAdapter =
            ChannelsPagerAdapter(parentFragmentManager, lifecycle)

        binding.viewPager.adapter = channelsPagerAdapter

        channelsPagerAdapter.updateFragments(
            listOf(
                ChannelsListFragment.newInstance(ChannelsListFragment.SUBSCRIBED),
                ChannelsListFragment.newInstance(ChannelsListFragment.ALL_STREAMS)
            )
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    companion object {
        fun newInstance() = ChannelsFragment()
    }
}
