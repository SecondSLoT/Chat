package com.secondslot.coursework.features.people.presenter

import android.util.Log
import com.secondslot.coursework.base.mvp.MoxyRxPresenter
import com.secondslot.coursework.domain.usecase.user.GetAllUsersUseCase
import com.secondslot.coursework.features.people.ui.UsersView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UsersPresenter @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase
) : MoxyRxPresenter<UsersView>() {

    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG, "onFirstViewAttach()")
        loadUsers()
        subscribeOnSearchChanges()
    }

    override fun attachView(view: UsersView) {
        super.attachView(view)
        Log.d(TAG, "attachView()")
    }

    override fun detachView(view: UsersView) {
        super.detachView(view)
        Log.d(TAG, "detachView()")
    }

    private fun loadUsers() {
        getAllUsersUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    if (it.isNullOrEmpty()) {
                        viewState.setStateLoading()
                    } else {
                        viewState.setStateResult(it)
                    }
                },
                onError = { viewState.setStateError(it) }
            )
            .disposeOnFinish()
    }

    fun searchUsers(searchQuery: String) {
        searchSubject.onNext(searchQuery)
    }

    private fun subscribeOnSearchChanges() {
        searchSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { viewState.setStateLoading() }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { searchQuery -> getAllUsersUseCase.execute(searchQuery) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { viewState.setStateResult(it) },
                onError = {
                    Log.d(TAG, "searchSubject error")
                    viewState.setStateError(it)
                }
            )
            .disposeOnFinish()
    }

    fun onRetry() {
        loadUsers()
        subscribeOnSearchChanges()
    }

    fun onUserClicked(userId: Int) {
        viewState.openUser(userId)
    }

    companion object {
        private const val TAG = "UsersPresenter"
    }
}
