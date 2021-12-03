package com.secondslot.coursework.features.channels.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentStreamsBinding
import com.secondslot.coursework.features.channels.adapter.StreamsPagerAdapter
import com.secondslot.coursework.features.channels.vm.StreamsViewModel
import com.secondslot.coursework.features.settings.SettingsFragment
import kotlinx.coroutines.flow.collect

class StreamsFragment : Fragment() {

    private val viewModel by viewModels<StreamsViewModel>()

    private var _binding: FragmentStreamsBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsBinding.inflate(inflater, container, false)
        initViews()
        setListeners()
        setObservers()
        return binding.root
    }

    private fun initViews() {
        binding.includedSearchView.settingsImageView.visibility = View.VISIBLE
        binding.includedSearchView.searchUsersEditText.hint =
            getString(R.string.channels_search_hint)

        val tabs: List<String> = listOf(
            getString(R.string.subscribed),
            getString(R.string.all_streams)
        )

        val streamsPagerAdapter = StreamsPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = streamsPagerAdapter
        streamsPagerAdapter.updateFragments(viewModel.streamsFragmentsList)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun setListeners() {

        binding.includedSearchView.settingsImageView.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }

        binding.includedSearchView.searchUsersEditText.doAfterTextChanged {
            val currentPosition = binding.viewPager.currentItem
            viewModel.searchStreams(currentPosition, it.toString())
        }

        binding.includedSearchView.searchImageView.setOnClickListener {
            val currentPosition = binding.viewPager.currentItem
            viewModel.searchStreams(
                currentPosition,
                binding.includedSearchView.searchUsersEditText.text.toString()
            )
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.onPageChanged()
            }
        })
    }

    private fun setObservers() {

        lifecycleScope.launchWhenStarted {
            viewModel.clearSearchFlow
                .collect { clearSearchViewEvent ->
                    clearSearchViewEvent.getContentIfNotHandled()?.let {
                        if (it) clearSearchView()
                    }
                }
        }
    }

    private fun clearSearchView() {
        binding.includedSearchView.searchUsersEditText.text.clear()
    }

    companion object {
        fun newInstance() = StreamsFragment()
    }
}
