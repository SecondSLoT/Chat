package com.secondslot.coursework.features.profile.ui

import com.secondslot.coursework.domain.model.User
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.SingleState

interface ProfileView : MvpView {

    @SingleState
    fun setStateLoading()

    @SingleState
    fun setStateResult(user: User)

    @SingleState
    fun setStateError(error: Throwable)
}
