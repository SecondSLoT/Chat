package com.secondslot.coursework.features.people.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.secondslot.coursework.R
import com.secondslot.coursework.base.mvp.MvpFragment
import com.secondslot.coursework.databinding.FragmentUsersBinding
import com.secondslot.coursework.di.GlobalDI
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.features.people.adapter.PeopleListAdapter
import com.secondslot.coursework.features.people.adapter.UsersItemDecoration
import com.secondslot.coursework.features.people.presenter.UsersContract
import com.secondslot.coursework.features.people.ui.UserState.*
import com.secondslot.coursework.features.profile.ui.ProfileFragment

class PeopleFragment :
    MvpFragment<UsersContract.UsersView, UsersContract.UsersPresenter>(),
    UsersContract.UsersView,
    OnUserClickListener {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val usersAdapter = PeopleListAdapter(this)

    private val presenter = GlobalDI.INSTANCE.getUsersPresenter()

    override fun getPresenter(): UsersContract.UsersPresenter = presenter

    override fun getMvpView(): UsersContract.UsersView = this

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        initViews()
        setListeners()
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

            includedRetryButton.retryButton.setOnClickListener { presenter.retry() }
        }
    }

    private fun searchUsers(searchQuery: String) {
        presenter.searchUsers(searchQuery)
    }

    override fun setStateLoading() {
        processFragmentState(Loading)
    }

    override fun setStateResult(users: List<User>) {
        processFragmentState(Result(users))
    }

    override fun setStateError(error: Throwable) {
        processFragmentState(Error(error))
    }

    private fun processFragmentState(state: UserState) {
        when (state) {
            is Result -> {
                usersAdapter.submitList(state.items.toList())

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

    override fun onUserClick(userId: Int) {
        presenter.onUserClicked(userId)
    }

    override fun openUser(userId: Int) {
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
