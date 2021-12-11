package com.secondslot.coursework.base.mvp.presenter

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxPresenter<View> : BasePresenter<View>() {

    private val compositeDisposable = CompositeDisposable()

    override fun detachView(isFinishing: Boolean) {
        if (isFinishing) {
            compositeDisposable.dispose()
        }
        super.detachView(isFinishing)
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
