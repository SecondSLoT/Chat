package com.secondslot.coursework.features.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentMainBinding
import com.secondslot.coursework.features.channels.ui.ChannelsFragment
import com.secondslot.coursework.features.people.PeopleFragment
import com.secondslot.coursework.features.profile.ProfileFragment

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        loadFragment(ChannelsFragment.newInstance())

        binding.bottomNavigationView.run {
            selectedItemId = R.id.action_channels
            setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_channels -> {
                        loadFragment(ChannelsFragment.newInstance())
                        true
                    }
                    R.id.action_people -> {
                        loadFragment(PeopleFragment.newInstance())
                        true
                    }
                    else -> {
                        loadFragment(ProfileFragment.newInstance())
                        true
                    }
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commitAllowingStateLoss()
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
