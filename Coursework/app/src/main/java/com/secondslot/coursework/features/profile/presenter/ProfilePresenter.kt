package com.secondslot.coursework.features.profile.presenter

import android.util.Log
import com.secondslot.coursework.base.mvp.presenter.MoxyRxPresenter
import com.secondslot.coursework.domain.usecase.user.GetOwnProfileUseCase
import com.secondslot.coursework.domain.usecase.user.GetProfileUseCase
import com.secondslot.coursework.features.profile.ui.ProfileView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getOwnProfileUseCase: GetOwnProfileUseCase
) : MoxyRxPresenter<ProfileView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG, "FirstAttachView()")
    }

    override fun attachView(view: ProfileView) {
        super.attachView(view)
        Log.d(TAG, "attachView()")
        Log.d(TAG, "Presenter: $this.")
    }

    override fun detachView(view: ProfileView) {
        super.detachView(view)
        Log.d(TAG, "detachView()")
    }

    fun loadProfile(userId: Int) {
        val profileObservable = if (userId != -1) {
            getProfileUseCase.execute(userId)
        } else {
            getOwnProfileUseCase.execute()
        }

        profileObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Log.d(TAG, "profileObservable onNext")
                    if (it.isNullOrEmpty()) {
                        viewState.setStateLoading()
                    } else {
                        viewState.setStateResult(it[0])
                    }
                },
                onError = { viewState.setStateError(it) },
                onComplete = { Log.d(TAG, "profileObservable OnComplete") }
            )
            .disposeOnFinish()
    }

    companion object {
        private const val TAG = "ProfilePresenter"
    }
}
