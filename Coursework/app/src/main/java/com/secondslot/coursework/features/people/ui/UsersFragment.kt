package com.secondslot.coursework.features.people.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.secondslot.coursework.App
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentUsersBinding
import com.secondslot.coursework.di.NavigatorFactory
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.features.people.adapter.PeopleListAdapter
import com.secondslot.coursework.features.people.adapter.UsersItemDecoration
import com.secondslot.coursework.features.people.di.DaggerUsersComponent
import com.secondslot.coursework.features.people.presenter.UsersPresenter
import com.secondslot.coursework.features.people.ui.UserState.Error
import com.secondslot.coursework.features.people.ui.UserState.Loading
import com.secondslot.coursework.features.people.ui.UserState.Result
import com.secondslot.coursework.navigation.AppNavigation
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class UsersFragment :
    MvpAppCompatFragment(),
    UsersView,
    OnUserClickListener {

    @Inject
    internal lateinit var presenterProvider: Provider<UsersPresenter>
    private val presenter: UsersPresenter by moxyPresenter { presenterProvider.get() }

    @Inject
    internal lateinit var navigationFactory: NavigatorFactory

    private lateinit var navigator: AppNavigation

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val usersAdapter = PeopleListAdapter(this)

    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val usersComponent = DaggerUsersComponent.factory().create(App.appComponent)
        usersComponent.inject(this)
        navigator = navigationFactory.create(requireActivity())

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

            swipeRefreshLayout.setOnRefreshListener {
                presenter.onRetry()
            }
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
                    recyclerView.isVisible = true
                    swipeRefreshLayout.isRefreshing = false
                }
                snackbar?.dismiss()
                snackbar = null
            }

            Loading -> {
                binding.run {
                    recyclerView.isVisible = false
                    shimmer.isVisible = true
                }
                snackbar?.dismiss()
                snackbar = null
            }

            is Error -> {
                binding.run {
                    shimmer.isVisible = false
                    recyclerView.isVisible = true
                    swipeRefreshLayout.isRefreshing = false
                }
                snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.update_data),
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(getString(R.string.retry_button)) {
                        presenter.onRetry()
                    }
                snackbar?.show()
            }
        }
    }

    override fun showSnackbar(show: Boolean) {
        if (show) snackbar?.show() else snackbar?.dismiss()
    }

    override fun onUserClick(userId: Int) {
        presenter.onUserClicked(userId)
    }

    override fun openUser(userId: Int) {
        navigator.navigateToProfileFragment(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
