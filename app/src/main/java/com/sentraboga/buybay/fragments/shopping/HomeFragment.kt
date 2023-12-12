package com.sentraboga.buybay.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.sentraboga.buybay.R
import com.sentraboga.buybay.adapters.HomeViewPagerAdp
import com.sentraboga.buybay.databinding.FragmentHomeBinding
import com.sentraboga.buybay.fragments.categories.CannedFragment
import com.sentraboga.buybay.fragments.categories.FreshFragment
import com.sentraboga.buybay.fragments.categories.FrozenFragment
import com.sentraboga.buybay.fragments.categories.MainCategoryFragment

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragment = arrayListOf<Fragment>(
            MainCategoryFragment(),
            FreshFragment(),
            FrozenFragment(),
            CannedFragment()
        )

        binding.viewPagerHome.isUserInputEnabled = false

        val viewPager2Adp = HomeViewPagerAdp(categoriesFragment, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = viewPager2Adp
        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome) { tab, position ->
            when(position) {
                0 -> tab.text = "Home"
                1 -> tab.text = "Fresh"
                2 -> tab.text = "Frozen"
                3 -> tab.text = "Canned"
            }
        }.attach()
    }

}