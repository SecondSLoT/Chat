package com.secondslot.coursework.features.channels.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.secondslot.coursework.App
import com.secondslot.coursework.R
import com.secondslot.coursework.base.mvp.MvpFragment
import com.secondslot.coursework.databinding.FragmentChannelsBinding
import com.secondslot.coursework.features.channels.adapter.StreamsPagerAdapter
import com.secondslot.coursework.features.channels.di.DaggerStreamsComponent
import com.secondslot.coursework.features.channels.presenter.StreamsContract
import javax.inject.Inject

class StreamsFragment :
    MvpFragment<StreamsContract.StreamsView, StreamsContract.StreamsPresenter>(),
    StreamsContract.StreamsView {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = requireNotNull(_binding)

    @Inject
    internal lateinit var presenter: StreamsContract.StreamsPresenter

    override fun getPresenter(): StreamsContract.StreamsPresenter = presenter

    override fun getMvpView(): StreamsContract.StreamsView = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val streamsComponent = DaggerStreamsComponent.factory().create(App.appComponent)
        streamsComponent.injectStreamsFragment(this)
    }

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
        channelsPagerAdapter.updateFragments(presenter.getStreamsFragments())

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

    companion object {
        fun newInstance() = StreamsFragment()
    }
}
