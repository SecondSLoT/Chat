package com.secondslot.coursework.features.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentMainBinding
import com.secondslot.coursework.features.channels.ui.StreamsFragment
import com.secondslot.coursework.features.people.ui.PeopleFragment
import com.secondslot.coursework.features.profile.ui.ProfileFragment

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

    override fun onResume() {
        super.onResume()
        changePage(binding.bottomNavigationView.selectedItemId)
    }

    private fun initViews() {
        binding.bottomNavigationView.run {
            selectedItemId = R.id.action_channels

            setOnItemSelectedListener { menuItem ->
                changePage(menuItem.itemId)
                true
            }
        }
    }

    private fun changePage(menuItemId: Int) {
        when (menuItemId) {
            R.id.action_channels -> {
                loadFragment(StreamsFragment.newInstance())
            }
            R.id.action_people -> {
                loadFragment(PeopleFragment.newInstance())
            }
            else -> {
                loadFragment(ProfileFragment.newInstance())
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
