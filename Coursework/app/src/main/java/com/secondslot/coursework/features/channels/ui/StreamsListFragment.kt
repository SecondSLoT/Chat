package com.secondslot.coursework.features.channels.ui

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.secondslot.coursework.App
import com.secondslot.coursework.R
import com.secondslot.coursework.base.mvp.MvpFragment
import com.secondslot.coursework.databinding.FragmentChannelsListBinding
import com.secondslot.coursework.features.channels.adapter.StreamsItemDecoration
import com.secondslot.coursework.features.channels.adapter.StreamsListAdapter
import com.secondslot.coursework.features.channels.di.DaggerStreamsComponent
import com.secondslot.coursework.features.channels.di.StreamsListFactory
import com.secondslot.coursework.features.channels.model.ExpandableStreamModel
import com.secondslot.coursework.features.channels.presenter.StreamsListPresenter
import com.secondslot.coursework.features.channels.ui.ChannelsState.*
import com.secondslot.coursework.features.chat.ui.ChatFragment
import javax.inject.Inject

class StreamsListFragment :
    MvpFragment<StreamsListView, StreamsListPresenter>(),
    StreamsListView,
    ExpandCollapseListener,
    SearchQueryListener,
    OnTopicClickListener {

    @Inject
    internal lateinit var presenterFactory: StreamsListFactory

    internal lateinit var presenter: StreamsListPresenter

    private var _binding: FragmentChannelsListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val streamsListAdapter = StreamsListAdapter(this, this)

    private var streamModelList = mutableListOf<ExpandableStreamModel>()

    override fun getPresenter(): StreamsListPresenter = presenter

    override fun getMvpView(): StreamsListView = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val streamsComponent = DaggerStreamsComponent.factory().create(App.appComponent)
        streamsComponent.injectStreamsListFragment(this)
        presenter = presenterFactory.create(
            arguments?.getString(CONTENT_KEY, "") ?: ""
        )

        val typedValue = TypedValue()
        requireActivity().run {
            theme.resolveAttribute(android.R.attr.statusBarColor, typedValue, true)
            window.statusBarColor = typedValue.data
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsListBinding.inflate(inflater, container, false)
        initViews()
        setListeners()
        return binding.root
    }

    private fun initViews() {
        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = streamsListAdapter
            addItemDecoration(StreamsItemDecoration())
            itemAnimator = null
        }
    }

    private fun setListeners() {
        binding.includedRetryButton.retryButton.setOnClickListener {
            presenter.retry()
        }
    }

    override fun setStateLoading() {
        processFragmentState(Loading)
    }

    override fun setStateResult(expandableStreamModel: List<ExpandableStreamModel>) {
        processFragmentState(Result(expandableStreamModel))
    }

    override fun setStateError(error: Throwable) {
        processFragmentState(Error(error))
    }

    private fun processFragmentState(state: ChannelsState) {
        when (state) {
            is Result -> {
                streamModelList = state.items.toMutableList()

                streamsListAdapter.submitList(streamModelList)
                binding.run {
                    shimmer.isVisible = false
                    includedRetryButton.retryButton.isVisible = false
                    recyclerView.isVisible = true
                }
            }

            Loading -> {
                binding.run {
                    recyclerView.isVisible = false
                    includedRetryButton.retryButton.isVisible = false
                    shimmer.isVisible = true
                }
            }

            is Error -> {
                Toast.makeText(requireContext(), state.error.message, Toast.LENGTH_SHORT).show()
                state.error.message?.let { Log.e(TAG, it) }

                binding.run {
                    shimmer.isVisible = false
                    recyclerView.isVisible = false
                    includedRetryButton.retryButton.isVisible = true
                }
            }
        }
    }

    override fun expandRow(position: Int) {
        val row = streamModelList[position]
        var nextPosition = position

        if (row.type == ExpandableStreamModel.PARENT) {
            for (child in row.stream.topics) {
                streamModelList.add(
                    ++nextPosition,
                    ExpandableStreamModel(ExpandableStreamModel.CHILD, child)
                )
            }
        }

        streamsListAdapter.submitList(streamModelList.toList())
    }

    override fun collapseRow(position: Int) {
        val row = streamModelList[position]
        val nextPosition = position + 1

        if (row.type == ExpandableStreamModel.PARENT) {
            outerloop@ while (true) {
                if (nextPosition == streamModelList.size ||
                    streamModelList[nextPosition].type == ExpandableStreamModel.PARENT
                ) {
                    break@outerloop
                }

                streamModelList.removeAt(nextPosition)
            }

            streamsListAdapter.submitList(streamModelList.toList())
        }
    }

    override fun search(searchQuery: String) {
        presenter.searchStreams(searchQuery)
    }

    override fun onTopicClicked(topicName: String, maxMessageId: Int, streamId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                ChatFragment.newInstance(topicName, maxMessageId, streamId)
            )
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val TAG = "StreamsListFragment"
        private const val CONTENT_KEY = "list_type"

        fun newInstance(contentKey: String): Fragment {
            return StreamsListFragment().apply {
                arguments = bundleOf(CONTENT_KEY to contentKey)
            }
        }
    }
}

internal sealed class ChannelsState {

    class Result(val items: List<ExpandableStreamModel>) : ChannelsState()

    object Loading : ChannelsState()

    class Error(val error: Throwable) : ChannelsState()
}
