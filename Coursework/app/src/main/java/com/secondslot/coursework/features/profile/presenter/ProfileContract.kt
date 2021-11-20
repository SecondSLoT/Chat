package com.secondslot.coursework.features.profile.presenter

import com.secondslot.coursework.base.mvp.presenter.Presenter
import com.secondslot.coursework.domain.model.User

interface ProfileContract {

    interface ProfileView {

        fun setStateLoading()

        fun setStateResult(user: User)

        fun setStateError(error: Throwable)
    }

    interface ProfilePresenter : Presenter<ProfileView> {
        fun loadProfile(userId: Int)
    }
}
