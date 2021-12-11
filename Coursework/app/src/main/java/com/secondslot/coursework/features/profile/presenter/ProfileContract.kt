package com.secondslot.coursework.features.profile.presenter

import com.secondslot.coursework.domain.model.User

interface ProfileView {

    fun setStateLoading()

    fun setStateResult(user: User)

    fun setStateError(error: Throwable)
}
