package com.secondslot.coursework.features.channels.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.secondslot.coursework.App
import com.secondslot.coursework.R
import com.secondslot.coursework.base.mvp.MvpFragment
import com.secondslot.coursework.databinding.FragmentStreamsBinding
import com.secondslot.coursework.features.channels.adapter.StreamsPagerAdapter
import com.secondslot.coursework.features.channels.di.DaggerStreamsComponent
import com.secondslot.coursework.features.channels.presenter.StreamsPresenter
import javax.inject.Inject

class StreamsFragment :
    MvpFragment<StreamsView, StreamsPresenter>(),
    StreamsView {

    private var _binding: FragmentStreamsBinding? = null
    private val binding get() = requireNotNull(_binding)

    @Inject
    internal lateinit var presenter: StreamsPresenter

    override fun getPresenter(): StreamsPresenter = presenter

    override fun getMvpView(): StreamsView = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val streamsComponent = DaggerStreamsComponent.factory().create(App.appComponent)
        streamsComponent.injectStreamsFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsBinding.inflate(inflater, container, false)
        setListeners()
        return binding.root
    }

    override fun initViews(streamsFragmentsList: List<Fragment>) {
        binding.includedSearchView.searchUsersEditText.hint =
            getString(R.string.channels_search_hint)

        val tabs: List<String> = listOf(
            getString(R.string.subscribed),
            getString(R.string.all_streams)
        )

        val channelsPagerAdapter = StreamsPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = channelsPagerAdapter
        channelsPagerAdapter.updateFragments(streamsFragmentsList)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun setListeners() {

        binding.includedSearchView.searchUsersEditText.doAfterTextChanged {
            val currentPosition = binding.viewPager.currentItem
            presenter.searchStreams(currentPosition, it.toString())
        }

        binding.includedSearchView.searchImageView.setOnClickListener {
            val currentPosition = binding.viewPager.currentItem
            presenter.searchStreams(
                currentPosition,
                binding.includedSearchView.searchUsersEditText.text.toString()
            )
        }
    }

    override fun clearSearchView() {
        binding.includedSearchView.searchUsersEditText.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = StreamsFragment()
    }
}
