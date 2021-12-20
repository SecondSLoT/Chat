package com.secondslot.coursework.features.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.secondslot.coursework.App
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.FragmentMainBinding
import com.secondslot.coursework.features.main.presenter.MainFragmentPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class MainFragment : MvpAppCompatFragment(), MainFragmentView {

    @Inject
    internal lateinit var presenterProvider: Provider<MainFragmentPresenter>
    private val presenter: MainFragmentPresenter by moxyPresenter { presenterProvider.get() }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.injectMainFragment(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            presenter.changePage(menuItem.itemId)
            true
        }
    }

    override fun loadFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commitAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
