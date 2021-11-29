package com.secondslot.coursework.features.channels.ui

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.tabs.TabLayoutMediator
import com.secondslot.coursework.App
import com.secondslot.coursework.R
import com.secondslot.coursework.base.mvp.MvpFragment
import com.secondslot.coursework.databinding.FragmentStreamsBinding
import com.secondslot.coursework.features.channels.adapter.StreamsPagerAdapter
import com.secondslot.coursework.features.channels.di.DaggerStreamsComponent
import com.secondslot.coursework.features.channels.vm.StreamsContract
import javax.inject.Inject

class StreamsFragment :
    MvpFragment<StreamsContract.StreamsView, StreamsContract.StreamsPresenter>(),
    StreamsContract.StreamsView {

    private var _binding: FragmentStreamsBinding? = null
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
        _binding = FragmentStreamsBinding.inflate(inflater, container, false)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun clearSearchView() {
        binding.includedSearchView.searchUsersEditText.text.clear()
    }

    companion object {
        fun newInstance() = StreamsFragment()
    }
}
