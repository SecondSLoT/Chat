package com.secondslot.coursework.features.profile.ui

import com.secondslot.coursework.domain.model.User

interface ProfileView {

    fun setStateLoading()

    fun setStateResult(user: User)

    fun setStateError(error: Throwable)
}
