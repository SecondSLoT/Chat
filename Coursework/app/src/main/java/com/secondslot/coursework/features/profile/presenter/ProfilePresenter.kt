package com.secondslot.coursework.features.profile.presenter

import android.util.Log
import com.secondslot.coursework.domain.usecase.GetOwnProfileUseCase
import com.secondslot.coursework.domain.usecase.GetProfileUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import com.secondslot.coursework.base.mvp.presenter.RxPresenter

class ProfilePresenter(
    private val getProfileUseCase: GetProfileUseCase,
    private val getOwnProfileUseCase: GetOwnProfileUseCase
) :
    RxPresenter<ProfileContract.ProfileView>(ProfileContract.ProfileView::class.java),
    ProfileContract.ProfilePresenter {

    override fun attachView(view: ProfileContract.ProfileView) {
        super.attachView(view)
        Log.d(TAG, "attachView()")
    }

    override fun detachView(isFinishing: Boolean) {
        super.detachView(isFinishing)
        Log.d(TAG, "detachView()")
    }

    override fun loadProfile(userId: Int) {
        Log.d(TAG, "loadProfile() invoked, userId = $userId")

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
                        view?.setStateLoading()
                    } else {
                        view?.setStateResult(it[0])
                    }
                },
                onError = { view?.setStateError(it) },
                onComplete = { Log.d(TAG, "profileObservable OnComplete")}
            )
            .disposeOnFinish()
    }

    companion object {
        private const val TAG = "ProfilePresenter"
    }
}
