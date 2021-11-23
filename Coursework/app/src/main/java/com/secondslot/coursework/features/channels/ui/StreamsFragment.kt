package com.secondslot.coursework.features.channels.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentChannelsBinding
import com.secondslot.coursework.features.channels.adapter.StreamsPagerAdapter
import com.secondslot.coursework.features.channels.presenter.StreamsListContract

class StreamsFragment : Fragment() {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val channelsListFragments = listOf(
        ChannelsListFragment.newInstance(StreamsListContract.SUBSCRIBED),
        ChannelsListFragment.newInstance(StreamsListContract.ALL_STREAMS)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        initViews()
        setListeners()
        return binding.root
    }

    private fun initViews() {
        binding.includedSearchView.searchUsersEditText.hint =
            getString(R.string.channels_search_hint)

        val tabs: List<String> = listOf(
            getString(R.string.subscribed),
            getString(R.string.all_streams)
        )

        val channelsPagerAdapter = StreamsPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = channelsPagerAdapter
        channelsPagerAdapter.updateFragments(channelsListFragments)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun setListeners() {
        binding.includedSearchView.searchUsersEditText.doAfterTextChanged {
            searchStreams(it.toString())
        }

        binding.includedSearchView.searchImageView.setOnClickListener {
            searchStreams(binding.includedSearchView.searchUsersEditText.text.toString())
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.includedSearchView.searchUsersEditText.text.clear()
            }
        })
    }

    private fun searchStreams(searchQuery: String) {
        channelsListFragments.forEach {
            (it as SearchQueryListener).search(searchQuery)
        }
    }

    companion object {
        fun newInstance() = StreamsFragment()
    }
}
