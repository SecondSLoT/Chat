package com.secondslot.coursework.features.main.presenter

import com.secondslot.coursework.R
import com.secondslot.coursework.features.channels.ui.StreamsFragment
import com.secondslot.coursework.features.main.ui.MainFragmentView
import com.secondslot.coursework.features.people.ui.UsersFragment
import com.secondslot.coursework.features.profile.ui.ProfileFragment
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class MainFragmentPresenter @Inject constructor() : MvpPresenter<MainFragmentView>() {

    private var lastSelectedItem: Int? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        changePage(R.id.action_channels)
    }

    fun changePage(menuItemId: Int) {
        when (menuItemId) {

            R.id.action_channels -> {
                if (lastSelectedItem != R.id.action_channels) {
                    viewState.loadFragment(StreamsFragment.newInstance())
                    lastSelectedItem = R.id.action_channels
                }
            }

            R.id.action_people -> {
                if (lastSelectedItem != R.id.action_people) {
                    viewState.loadFragment(UsersFragment.newInstance())
                    lastSelectedItem = R.id.action_people
                }
            }

            else -> {
                if (lastSelectedItem != R.id.action_profile) {
                    viewState.loadFragment(ProfileFragment.newInstance())
                    lastSelectedItem = R.id.action_profile
                }
            }
        }
    }
}
