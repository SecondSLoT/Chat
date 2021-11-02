package com.secondslot.coursework.features.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentProfileBinding
import com.secondslot.coursework.domain.model.User
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val userId = arguments?.getInt(USER_ID, 0) ?: 0
        initViews(userId)
        setListeners()
        setObservers(userId)
        return binding.root
    }

    private fun initViews(userId: Int) {
        if (userId == 0) {
            binding.toolbar.isVisible = false
        } else {
            binding.logOutButton.isVisible = false
        }
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setObservers(userId: Int) {
        val profileSingle = getProfileUseCase.execute(userId)
        profileSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { processFragmentState(Loading) }
            .subscribeBy(
                onSuccess = { processFragmentState(Result(it)) },
                onError = { processFragmentState(Error(it)) }
            )
            .addTo(compositeDisposable)
    }

    private fun processFragmentState(state: ProfileState) {
        when (state) {
            is Result -> {
                binding.run {
                    userPhoto.loadImage(state.user.userPhoto)
                    usernameTextView.text = state.user.username
                    aboutTextView.text = state.user.about

                    shimmer.isVisible = false
                    group.isVisible = true
                }

                if (state.user.status == User.STATUS_ONLINE) {
                    binding.run {
                        statusTextView.text = getString(R.string.status_online)
                        statusTextView.setTextColor(
                            getColor(requireContext(), R.color.status_online))
                    }
                } else {
                    binding.run {
                        statusTextView.text = getString(R.string.status_offline)
                        statusTextView.setTextColor(
                            getColor(requireContext(), R.color.email_text))
                    }
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
        private const val USER_ID = "user_id"

        fun newInstance(userId: Int): ProfileFragment {
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
