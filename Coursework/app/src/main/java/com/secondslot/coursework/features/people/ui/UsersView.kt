package com.secondslot.coursework.features.people.ui

import com.secondslot.coursework.domain.model.User
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.SingleState

interface UsersView : MvpView {

    @SingleState
    fun setStateLoading()

    @SingleState
    fun setStateResult(users: List<User>)

    @SingleState
    fun setStateError(error: Throwable)

    @OneExecution
    fun openUser(userId: Int)
}
