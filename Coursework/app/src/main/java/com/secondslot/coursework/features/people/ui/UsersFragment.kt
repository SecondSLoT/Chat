package com.secondslot.coursework.features.people.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentUsersBinding
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.usecase.GetAllUsersUseCase
import com.secondslot.coursework.features.people.adapter.PeopleListAdapter
import com.secondslot.coursework.features.people.adapter.UsersItemDecoration
import com.secondslot.coursework.features.people.ui.UserState.Error
import com.secondslot.coursework.features.people.ui.UserState.Loading
import com.secondslot.coursework.features.people.ui.UserState.Result
import com.secondslot.coursework.features.profile.ui.ProfileFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class PeopleFragment : Fragment(), OnUserClickListener {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val searchSubject: PublishSubject<String> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()

    private val getAllUsersUseCase = GetAllUsersUseCase()
    private val usersAdapter = PeopleListAdapter(this)

    private var users = listOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        initViews()
        setListeners()
        setObservers()
        return binding.root
    }

    private fun initViews() {
        binding.includedSearchView.searchUsersEditText.hint = getString(R.string.users_search_hint)

        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter
            addItemDecoration(UsersItemDecoration())
        }
    }

    private fun setListeners() {
        binding.run {
            includedSearchView.searchUsersEditText.doAfterTextChanged {
                searchUsers(it.toString())
            }

            includedRetryButton.retryButton.setOnClickListener { getUsers() }
        }
    }

    private fun searchUsers(searchQuery: String) {
        searchSubject.onNext(searchQuery)
    }

    private fun setObservers() {
        getUsers()
        subscribeOnSearchChanges()
    }

    private fun getUsers() {
        val usersObservable = getAllUsersUseCase.execute()
        usersObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { processFragmentState(Loading) }
            .subscribeBy(
                onNext = { processFragmentState(Result(it)) },
                onError = { processFragmentState(Error(it)) }
            )
            .addTo(compositeDisposable)
    }

    private fun processFragmentState(state: UserState) {
        when (state) {
            is Result -> {
                users = state.items
                usersAdapter.submitList(users.toList())

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
            .switchMap { searchQuery -> getAllUsersUseCase.execute(searchQuery) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { processFragmentState(Result(it)) },
                onError = { processFragmentState(Error(it)) }
            )
            .addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onUserClick(userId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, ProfileFragment.newInstance(userId))
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    companion object {
        fun newInstance() = PeopleFragment()
    }
}

internal sealed class UserState {

    class Result(val items: List<User>) : UserState()

    object Loading : UserState()

    class Error(val error: Throwable) : UserState()
}
