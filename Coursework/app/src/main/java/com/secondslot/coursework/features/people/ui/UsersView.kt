package com.secondslot.coursework.features.people.ui

import com.secondslot.coursework.domain.model.User

interface UsersView {

    fun setStateLoading()

    fun setStateResult(users: List<User>)

    fun setStateError(error: Throwable)

    fun openUser(userId: Int)
}
