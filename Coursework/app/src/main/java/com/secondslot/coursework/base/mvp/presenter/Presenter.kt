package ru.alexkorrnd.tinkofffintechapp.presentation.base.mvp.presenter

interface Presenter<View> {

    fun attachView(view: View)

    fun detachView(isFinishing: Boolean)
}