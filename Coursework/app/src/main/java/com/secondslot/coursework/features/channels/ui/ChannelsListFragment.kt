package com.secondslot.coursework.features.channels.ui

import android.os.Bundle
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
import com.secondslot.coursework.domain.model.ExpandableChannelModel
import com.secondslot.coursework.domain.usecase.GetAllStreamsUseCase
import com.secondslot.coursework.domain.usecase.GetChannelsUseCase
import com.secondslot.coursework.features.channels.adapter.ChannelsItemDecoration
import com.secondslot.coursework.features.channels.adapter.ChannelsListAdapter
import com.secondslot.coursework.features.channels.ui.ChannelsState.*
import com.secondslot.coursework.features.chat.ui.ChatFragment
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

    private val getChannelsUseCase = GetChannelsUseCase()
    private val getAllStreamsUseCase = GetAllStreamsUseCase()
    private val channelsListAdapter = ChannelsListAdapter(this, this)

    private var searchSubject: PublishSubject<String> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()

    private var channels = mutableListOf<ExpandableChannelModel>()

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
            adapter = channelsListAdapter
            addItemDecoration(ChannelsItemDecoration())
            itemAnimator = null
        }
    }

    private fun setListeners() {
        binding.includedRetryButton.retryButton.setOnClickListener {
            getChannels()
        }
    }

    private fun setObservers() {
        getChannels()
        subscribeOnSearchChanges()
    }

    private fun getChannels() {
        val channelsObservable = when (arguments?.getString(CONTENT_KEY, "")) {
            SUBSCRIBED -> getChannelsUseCase.execute()
            else -> getAllStreamsUseCase.execute()
        }

        channelsObservable
            .subscribeOn(Schedulers.io())
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
                channels = state.items.toMutableList()

                channelsListAdapter.submitList(channels)
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
                    SUBSCRIBED -> getChannelsUseCase.execute(searchQuery)
                    else -> getAllStreamsUseCase.execute(searchQuery)
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
        val row = channels[position]
        var nextPosition = position

        if (row.type == ExpandableChannelModel.PARENT) {
            for (child in row.channelGroup.channels) {
                channels.add(
                    ++nextPosition,
                    ExpandableChannelModel(ExpandableChannelModel.CHILD, child)
                )
            }
        }

        channelsListAdapter.submitList(channels.toList())
    }

    override fun collapseRow(position: Int) {
        val row = channels[position]
        val nextPosition = position + 1

        if (row.type == ExpandableChannelModel.PARENT) {
            outerloop@ while (true) {
                if (nextPosition == channels.size ||
                    channels[nextPosition].type == ExpandableChannelModel.PARENT
                ) {
                    break@outerloop
                }

                channels.removeAt(nextPosition)
            }

            channelsListAdapter.submitList(channels.toList())
        }
    }

    override fun search(searchQuery: String) {
        searchSubject.onNext(searchQuery)
    }

    override fun onChannelClicked(channelId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, ChatFragment.newInstance(channelId))
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
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

internal sealed class ChannelsState {

    class Result(val items: List<ExpandableChannelModel>) : ChannelsState()

    object Loading : ChannelsState()

    class Error(val error: Throwable) : ChannelsState()
}
