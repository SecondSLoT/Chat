package com.secondslot.coursework.features.people.presenter

import android.util.Log
import com.secondslot.coursework.base.mvp.presenter.RxPresenter
import com.secondslot.coursework.domain.usecase.GetAllUsersUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UsersPresenter @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase
) :
    RxPresenter<UsersContract.UsersView>(UsersContract.UsersView::class.java),
    UsersContract.UsersPresenter {

    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    override fun attachView(view: UsersContract.UsersView) {
        super.attachView(view)
        Log.d(TAG, "attachView()")
        loadUsers()
        subscribeOnSearchChanges()
    }

    override fun detachView(isFinishing: Boolean) {
        super.detachView(isFinishing)
        Log.d(TAG, "detachView()")
    }

    private fun loadUsers() {
        getAllUsersUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    if (it.isNullOrEmpty()) {
                        view?.setStateLoading()
                    } else {
                        view?.setStateResult(it)
                    }
                },
                onError = { view?.setStateError(it) }
            )
            .disposeOnFinish()
    }

    override fun searchUsers(searchQuery: String) {
        searchSubject.onNext(searchQuery)
    }

    private fun subscribeOnSearchChanges() {
        searchSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { view?.setStateLoading() }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { searchQuery -> getAllUsersUseCase.execute(searchQuery) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { view?.setStateResult(it) },
                onError = {
                    Log.d(TAG, "searchSubject error")
                    view?.setStateError(it)
                }
            )
            .disposeOnFinish()
    }

    override fun retry() {
        loadUsers()
        subscribeOnSearchChanges()
    }

    override fun onUserClicked(userId: Int) {
        view?.openUser(userId)
    }

    companion object {
        private const val TAG = "UsersPresenter"
    }
}
