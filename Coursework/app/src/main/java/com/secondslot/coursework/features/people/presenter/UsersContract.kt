package com.secondslot.coursework.features.people.presenter

import com.secondslot.coursework.base.mvp.presenter.Presenter
import com.secondslot.coursework.domain.model.User

interface UsersContract {

    interface UsersView {

        fun setStateLoading()

        fun setStateResult(users: List<User>)

        fun setStateError(error: Throwable)

        fun openUser(userId: Int)
    }

    interface UsersPresenter : Presenter<UsersView> {

        fun retry()

        fun searchUsers(searchQuery: String)

        fun onUserClicked(userId: Int)
    }
}
