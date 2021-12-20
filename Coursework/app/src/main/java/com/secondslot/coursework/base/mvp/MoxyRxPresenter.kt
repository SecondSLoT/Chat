package com.secondslot.coursework.base.mvp

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpPresenter
import moxy.MvpView

abstract class MoxyRxPresenter<T : MvpView> : MvpPresenter<T>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    protected fun removeDisposable(disposable: Disposable?) {
        disposable?.let {
            compositeDisposable.remove(it)
        }
    }

    protected fun Disposable.disposeOnFinish(): Disposable {
        compositeDisposable += this
        return this
    }

    protected fun dispose(disposable: Disposable) {
        if (!compositeDisposable.remove(disposable)) {
            disposable.dispose()
        }
    }

    private operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        add(disposable)
    }
}
