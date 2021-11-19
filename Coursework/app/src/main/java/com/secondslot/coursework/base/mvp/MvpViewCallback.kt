package ru.alexkorrnd.tinkofffintechapp.presentation.base.mvp

import ru.alexkorrnd.tinkofffintechapp.presentation.base.mvp.presenter.Presenter

interface MvpViewCallback<View, P: Presenter<View>> {

    fun getPresenter(): P

    fun getMvpView(): View

}