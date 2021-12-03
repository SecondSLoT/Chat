package com.secondslot.coursework.features.people.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.secondslot.coursework.App
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentUsersBinding
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.features.people.adapter.PeopleListAdapter
import com.secondslot.coursework.features.people.adapter.UsersItemDecoration
import com.secondslot.coursework.features.people.di.DaggerUsersComponent
import com.secondslot.coursework.features.people.ui.UserState.Error
import com.secondslot.coursework.features.people.ui.UserState.Loading
import com.secondslot.coursework.features.people.ui.UserState.Result
import com.secondslot.coursework.features.people.vm.UsersViewModel
import com.secondslot.coursework.features.profile.ui.ProfileFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class UsersFragment : Fragment(), OnUserClickListener {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _viewModel: UsersViewModel? = null
    private val viewModel get() = requireNotNull(_viewModel)

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val usersAdapter = PeopleListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val usersComponent = DaggerUsersComponent.factory().create(App.appComponent)
        usersComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this, viewModelFactory)[UsersViewModel::class.java]

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

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun setListeners() {
        binding.run {
            includedSearchView.searchUsersEditText.doAfterTextChanged {
                searchUsers(it.toString())
            }

            includedRetryButton.retryButton.setOnClickListener { viewModel.onRetryClicked() }
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun setObservers() {
        observeUsers()

        lifecycleScope.run {
            launchWhenStarted {
                viewModel.openUserFlow.collect { userIdEvent ->
                    userIdEvent.getContentIfNotHandled()?.let {
                        if (it != -1) openUser(it)
                    }
                }
            }

            launchWhenStarted {
                viewModel.retryFlow.collect { retryEvent ->
                    retryEvent.getContentIfNotHandled()?.let {
                        if (it) observeUsers()
                    }
                }
            }
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun observeUsers() {
        lifecycleScope.run {
            launchWhenStarted {
                viewModel.loadUsers()
                    .catch { processFragmentState(Error(it)) }
                    .collect {
                        if (it.isNullOrEmpty()) {
                            processFragmentState(Loading)
                        } else {
                            processFragmentState(Result(it))
                        }
                    }
            }

            var justLaunched = true
            launchWhenStarted {
                viewModel.observeSearchChanges()
                    .catch { processFragmentState(Error(it)) }
                    .collect {
                        if (justLaunched) {
                            justLaunched = false
                        } else {
                            processFragmentState(Result(it))
                        }
                    }
            }
        }
    }

    private fun searchUsers(searchQuery: String) {
        viewModel.searchUsers(searchQuery)
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
        viewModel.onUserClicked(userId)
    }

    private fun openUser(userId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, ProfileFragment.newInstance(userId))
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    companion object {
        fun newInstance() = UsersFragment()
    }
}

internal sealed class UserState {

    class Result(val items: List<User>) : UserState()

    object Loading : UserState()

    class Error(val error: Throwable) : UserState()
}
