package com.secondslot.coursework.features.main.ui

import androidx.fragment.app.Fragment
import moxy.MvpView
import moxy.viewstate.strategy.alias.SingleState

interface MainFragmentView : MvpView {

    @SingleState
    fun loadFragment(fragment: Fragment)
}
