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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.secondslot.coursework.App
import com.secondslot.coursework.databinding.FragmentProfileBinding
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.extentions.loadImage
import com.secondslot.coursework.features.profile.di.DaggerProfileComponent
import com.secondslot.coursework.features.profile.ui.ProfileState.Error
import com.secondslot.coursework.features.profile.ui.ProfileState.Loading
import com.secondslot.coursework.features.profile.ui.ProfileState.Result
import com.secondslot.coursework.features.profile.vm.ProfileViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _viewModel: ProfileViewModel? = null
    private val viewModel get() = requireNotNull(_viewModel)

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var myId = -1
    private val userId: Int by lazy { arguments?.getInt(USER_ID, 0) ?: 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val profileComponent = DaggerProfileComponent.factory().create(App.appComponent)
        profileComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        initViews()
        setListeners()
        setObservers()
        return binding.root
    }

    private fun initViews() {
        if (userId == -1) {
            binding.toolbar.isVisible = false
        }
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setObservers() {

        lifecycleScope.launchWhenStarted {
            viewModel.loadProfile(userId)
                .catch { processFragmentState(Error(it)) }
                .collect {
                if (it.isNullOrEmpty()) {
                    processFragmentState(Loading)
                } else {
                    processFragmentState(Result(it[0]))
                }
            }
        }
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
                Log.e(TAG, "Error: ${state.error.message}")
                Toast.makeText(requireContext(), state.error.message, Toast.LENGTH_SHORT).show()
                binding.run {
                    shimmer.isVisible = false
                    group.isVisible = true
                }
            }
        }
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
