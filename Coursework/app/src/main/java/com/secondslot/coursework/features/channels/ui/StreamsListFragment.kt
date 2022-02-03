package com.secondslot.coursework.features.channels.ui

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.secondslot.coursework.App
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentStreamsListBinding
import com.secondslot.coursework.di.NavigatorFactory
import com.secondslot.coursework.features.channels.adapter.StreamsItemDecoration
import com.secondslot.coursework.features.channels.adapter.StreamsListAdapter
import com.secondslot.coursework.features.channels.di.DaggerStreamsComponent
import com.secondslot.coursework.features.channels.di.StreamsListPresenterFactory
import com.secondslot.coursework.features.channels.model.ExpandableStreamModel
import com.secondslot.coursework.features.channels.presenter.StreamsListPresenter
import com.secondslot.coursework.features.channels.ui.ChannelsState.Error
import com.secondslot.coursework.features.channels.ui.ChannelsState.Loading
import com.secondslot.coursework.features.channels.ui.ChannelsState.Result
import com.secondslot.coursework.navigation.AppNavigation
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

class StreamsListFragment :
    MvpAppCompatFragment(),
    StreamsListView,
    ExpandCollapseListener,
    SearchQueryListener,
    OnTopicClickListener {

    @Inject
    internal lateinit var presenterFactory: StreamsListPresenterFactory

    private val presenter: StreamsListPresenter by moxyPresenter {
        presenterFactory.create(arguments?.getString(CONTENT_KEY, "") ?: "")
    }

    @Inject
    internal lateinit var navigationFactory: NavigatorFactory

    private lateinit var navigator: AppNavigation

    private var _binding: FragmentStreamsListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val streamsListAdapter = StreamsListAdapter(this, this)

    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val streamsComponent = DaggerStreamsComponent.factory().create(App.appComponent)
        streamsComponent.injectStreamsListFragment(this)
        navigator = navigationFactory.create(requireActivity())

        val typedValue = TypedValue()
        requireActivity().run {
            theme.resolveAttribute(android.R.attr.statusBarColor, typedValue, true)
            window.statusBarColor = typedValue.data
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsListBinding.inflate(inflater, container, false)
        initViews()
        setListeners()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
        val isSnackbarShown = snackbar?.isShown ?: false
        if (snackbar != null && !isSnackbarShown) presenter.onRetryClicked()
    }

    private fun initViews() {
        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = streamsListAdapter
            addItemDecoration(StreamsItemDecoration())
            itemAnimator = null
        }
    }

    private fun setListeners() = binding.run {

        fab.setOnClickListener {
            presenter.onFabClicked()
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    presenter.onScrollUp()
                }

                if (dy > 0) {
                    presenter.onScrollDown(
                        (binding.recyclerView.layoutManager as LinearLayoutManager)
                            .findLastCompletelyVisibleItemPosition()
                    )
                }
            }
        })
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
                Log.d(TAG, "State result")
                submitStreamsList(state.items)
                binding.run {
                    shimmer.isVisible = false
                    recyclerView.isVisible = true
                }
                snackbar?.dismiss()
                snackbar = null
            }

            Loading -> {
                Log.d(TAG, "State loading")
                binding.run {
                    recyclerView.isVisible = false
                    shimmer.isVisible = true
                }
                snackbar?.dismiss()
                snackbar = null
            }

            is Error -> {
                Log.d(TAG, "State error")
                state.error.message?.let { Log.e(TAG, it) }

                binding.run {
                    shimmer.isVisible = false
                    recyclerView.isVisible = true
                }
                snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.update_data),
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(getString(R.string.retry_button)) {
                        presenter.onRetryClicked()
                    }
                snackbar?.show()
            }
        }
    }

    override fun showSnackbar(show: Boolean) {
        if (show) snackbar?.show() else snackbar?.dismiss()
    }

    override fun expandRow(position: Int) {
        presenter.onExpandRow(position)
    }

    override fun collapseRow(position: Int) {
        presenter.onCollapseRow(position)
    }

    override fun submitStreamsList(expandableStreamModel: List<ExpandableStreamModel>) {
        streamsListAdapter.submitList(expandableStreamModel)
    }

    override fun search(searchQuery: String) {
        presenter.searchStreams(searchQuery)
    }

    override fun onTopicClicked(topicName: String, maxMessageId: Int, streamId: Int) {
        presenter.onTopicClicked(topicName, maxMessageId, streamId)
    }

    override fun openChat(topicName: String, maxMessageId: Int, streamId: Int) {
        navigator.navigateToChatFragment(topicName, maxMessageId, streamId)
    }

    override fun showFab(isShown: Boolean) {
        if (isShown) binding.fab.show() else binding.fab.hide()
    }

    override fun openCreateStreamDialog() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CREATE_STREAM_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            Log.d(TAG, "get fragment result")
            val streamName = bundle.getString(STREAM_NAME_KEY, "")
            val description = bundle.getString(DESCRIPTION_KEY, "")
            presenter.onCreateNewStream(streamName, description)
        }

        navigator.navigateToCreateStreamDialog(
            requestKey = CREATE_STREAM_REQUEST_KEY,
            streamNameKey = STREAM_NAME_KEY,
            descriptionKey = DESCRIPTION_KEY
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val TAG = "StreamsListFragment"
        private const val CONTENT_KEY = "list_type"
        private const val CREATE_STREAM_REQUEST_KEY = "create_stream_result_key"
        private const val STREAM_NAME_KEY = "stream_name_key"
        private const val DESCRIPTION_KEY = "description_key"

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
