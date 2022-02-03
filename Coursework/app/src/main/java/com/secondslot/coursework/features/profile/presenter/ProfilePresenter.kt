package com.secondslot.coursework.features.profile.presenter

import android.util.Log
import com.secondslot.coursework.base.mvp.MoxyRxPresenter
import com.secondslot.coursework.domain.usecase.user.GetOwnProfileUseCase
import com.secondslot.coursework.domain.usecase.user.GetProfileUseCase
import com.secondslot.coursework.features.profile.ui.ProfileView
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState

@InjectViewState
class ProfilePresenter @AssistedInject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getOwnProfileUseCase: GetOwnProfileUseCase,
    @Assisted private val userId: Int
) : MoxyRxPresenter<ProfileView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadProfile()
        Log.d(TAG, "FirstAttachView()")
    }

    override fun attachView(view: ProfileView) {
        super.attachView(view)
        Log.d(TAG, "attachView(), presenter: $this")
    }

    override fun detachView(view: ProfileView) {
        super.detachView(view)
        Log.d(TAG, "detachView()")
    }

    private fun loadProfile() {
        val profileObservable = if (userId != -1) {
            getProfileUseCase.execute(userId)
        } else {
            getOwnProfileUseCase.execute()
        }

        profileObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
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

    fun onRetryClicked() { loadProfile() }

    companion object {
        private const val TAG = "ProfilePresenter"
    }
}
