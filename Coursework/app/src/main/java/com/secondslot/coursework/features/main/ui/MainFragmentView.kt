package com.secondslot.coursework.features.main.ui

import androidx.fragment.app.Fragment
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface MainFragmentView : MvpView {

    @OneExecution
    fun loadFragment(fragment: Fragment)
}
