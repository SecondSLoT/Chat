package ru.alexkorrnd.tinkofffintechapp.presentation.base.mvp.presenter

import androidx.annotation.CallSuper

abstract class BasePresenter<View> protected constructor(
    viewClass: Class<View>
): Presenter<View> {

    private var view: View? = null

    @CallSuper
    override fun attachView(view: View) {
        this.view = view
    }

    @CallSuper
    override fun detachView(isFinishing: Boolean) {
        view = null
    }

    fun hasView(): Boolean {
        return view != null
    }
}
