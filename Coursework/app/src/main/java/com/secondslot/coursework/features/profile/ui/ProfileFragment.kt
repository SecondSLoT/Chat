package com.secondslot.coursework.features.profile.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.secondslot.coursework.databinding.FragmentProfileBinding
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.usecase.GetOwnProfileUseCase
import com.secondslot.coursework.domain.usecase.GetProfileUseCase
import com.secondslot.coursework.extentions.loadImage
import com.secondslot.coursework.features.profile.ui.ProfileState.Error
import com.secondslot.coursework.features.profile.ui.ProfileState.Loading
import com.secondslot.coursework.features.profile.ui.ProfileState.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val compositeDisposable = CompositeDisposable()

    private val getProfileUseCase = GetProfileUseCase()
    private val getOwnProfileUseCase = GetOwnProfileUseCase()

    private var myId: Int = -1

    private val userId: Int by lazy { arguments?.getInt(USER_ID, 0) ?: 0 }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        initViews(userId)
        setListeners()
        setObservers(userId)
        return binding.root
    }

    private fun initViews(userId: Int) {
        if (userId == -1) {
            binding.toolbar.isVisible = false
        }
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setObservers(userId: Int) {
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
                        processFragmentState(Loading)
                    } else {
                        processFragmentState(Result(it[0]))
                    }
                },
                onError = { processFragmentState(Error(it)) }
            )
            .addTo(compositeDisposable)
    }

    private fun processFragmentState(state: ProfileState) {
        when (state) {
            is Result -> {
                binding.run {
                    userPhoto.loadImage(state.user.avatarUrl ?: "")
                    usernameTextView.text = state.user.fullName

                    shimmer.isVisible = false
                    group.isVisible = true

                    if (userId == -1) myId = state.user.userId
                }
            }

            Loading -> {
                binding.run {
                    group.isVisible = false
                    shimmer.isVisible = true
                }
            }

            is Error -> {
                Toast.makeText(requireContext(), state.error.message, Toast.LENGTH_SHORT).show()
                binding.run {
                    shimmer.isVisible = false
                    group.isVisible = true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        private const val TAG = "ProfileFragment"

        private const val USER_ID = "user_id"

        /**
         * @param userId: if userId is not defined, it's value will be set to -1.
         * This means that this fragment is for an own profile screen
         */
        fun newInstance(userId: Int = -1): ProfileFragment {
            return ProfileFragment().apply {
                arguments = bundleOf(USER_ID to userId)
            }
        }
    }
}

internal sealed class ProfileState {

    class Result(val user: User) : ProfileState()

    object Loading : ProfileState()

    class Error(val error: Throwable) : ProfileState()
}
