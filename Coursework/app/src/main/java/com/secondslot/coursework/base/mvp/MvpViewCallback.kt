package com.secondslot.coursework.base.mvp

import com.secondslot.coursework.base.mvp.presenter.Presenter

interface MvpViewCallback<View, P: Presenter<View>> {

    fun getPresenter(): P

    fun getMvpView(): View

}
