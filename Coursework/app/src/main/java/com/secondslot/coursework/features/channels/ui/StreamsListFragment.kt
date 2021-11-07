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
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentChannelsListBinding
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.usecase.GetAllStreamsUseCase
import com.secondslot.coursework.domain.usecase.GetSubscribedStreamsUseCase
import com.secondslot.coursework.features.channels.adapter.ChannelsItemDecoration
import com.secondslot.coursework.features.channels.adapter.StreamsListAdapter
import com.secondslot.coursework.features.channels.model.ExpandableStreamModel
import com.secondslot.coursework.features.channels.ui.ChannelsState.*
import com.secondslot.coursework.features.chat.ui.ChatFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class ChannelsListFragment : Fragment(), ExpandCollapseListener, SearchQueryListener,
    OnChatClickListener {

    private var _binding: FragmentChannelsListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val getSubscribedStreamsUseCase = GetSubscribedStreamsUseCase()
    private val getAllStreamsUseCase = GetAllStreamsUseCase()
    private val streamsListAdapter = StreamsListAdapter(this, this)

    private var searchSubject: PublishSubject<String> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()

    private var streamModelList = mutableListOf<ExpandableStreamModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        setObservers()
        return binding.root
    }

    private fun initViews() {
        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = streamsListAdapter
            addItemDecoration(ChannelsItemDecoration())
            itemAnimator = null
        }
    }

    private fun setListeners() {
        binding.includedRetryButton.retryButton.setOnClickListener {
            setObservers()
        }
    }

    private fun setObservers() {
        getStreams()
        subscribeOnSearchChanges()
    }

    private fun getStreams() {
        val streamsObservable: Observable<List<Stream>> =
            when (arguments?.getString(CONTENT_KEY, "")) {
                SUBSCRIBED -> getSubscribedStreamsUseCase.execute()
                else -> getAllStreamsUseCase.execute()
            }

        streamsObservable
            .subscribeOn(Schedulers.io())
            .map { ExpandableStreamModel.fromStream(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { processFragmentState(Loading) }
            .subscribeBy(
                onNext = { processFragmentState(Result(it)) },
                onError = { processFragmentState(Error(it)) }
            )
            .addTo(compositeDisposable)
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

    private fun subscribeOnSearchChanges() {
        searchSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { processFragmentState(Loading) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery ->
                when (arguments?.getString(CONTENT_KEY, "")) {
                    SUBSCRIBED -> {
                        getSubscribedStreamsUseCase.execute(searchQuery)
                            .map { ExpandableStreamModel.fromStream(it) }
                    }
                    else -> {
                        getAllStreamsUseCase.execute(searchQuery)
                            .map { ExpandableStreamModel.fromStream(it) }
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { processFragmentState(Result(it)) },
                onError = { processFragmentState(Error(it)) }
            )
            .addTo(compositeDisposable)
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
        searchSubject.onNext(searchQuery)
    }

    override fun onChannelClicked(channelId: Int, topic: String) {

        requireActivity().run {


            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ChatFragment.newInstance(channelId, topic))
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {

        private const val TAG = "TAG"
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

internal sealed class ChannelsState {

    class Result(val items: List<ExpandableStreamModel>) : ChannelsState()

    object Loading : ChannelsState()

    class Error(val error: Throwable) : ChannelsState()
}
