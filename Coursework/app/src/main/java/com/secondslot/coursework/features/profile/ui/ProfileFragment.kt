package com.secondslot.coursework.features.profile.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.secondslot.coursework.App
import com.secondslot.coursework.databinding.FragmentProfileBinding
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.extentions.loadImage
import com.secondslot.coursework.features.profile.di.DaggerProfileComponent
import com.secondslot.coursework.features.profile.di.ProfilePresenterFactory
import com.secondslot.coursework.features.profile.presenter.ProfilePresenter
import com.secondslot.coursework.features.profile.ui.ProfileState.Error
import com.secondslot.coursework.features.profile.ui.ProfileState.Loading
import com.secondslot.coursework.features.profile.ui.ProfileState.Result
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

class ProfileFragment : MvpAppCompatFragment(), ProfileView {

    @Inject
    internal lateinit var presenterFactory: ProfilePresenterFactory
    private val presenter: ProfilePresenter by moxyPresenter {
        presenterFactory.create(userId)
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var myId = -1
    private val userId: Int by lazy { arguments?.getInt(USER_ID, 0) ?: 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        val profileComponent = DaggerProfileComponent.factory().create(App.appComponent)
        profileComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        initViews(userId)
        setListeners()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, this.toString())
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

    override fun setStateLoading() {
        processFragmentState(Loading)
    }

    override fun setStateResult(user: User) {
        processFragmentState(Result(user))
    }

    override fun setStateError(error: Throwable) {
        processFragmentState(Error(error))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ProfileFragment"
        private const val USER_ID = "user_id"

        private val instance: ProfileFragment? = null

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
