package com.sentraboga.buybay.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.sentraboga.buybay.R
import com.sentraboga.buybay.activities.ShoppingActivity
import com.sentraboga.buybay.databinding.FragmentIntroductionBinding
import com.sentraboga.buybay.databinding.FragmentLoginBinding
import com.sentraboga.buybay.viewmodel.IntroductionViewModel
import com.sentraboga.buybay.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPT_FRAGMENT
import com.sentraboga.buybay.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class IntroductionFragment: Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect {
                when (it){
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    ACCOUNT_OPT_FRAGMENT -> {
                        findNavController().navigate(it)
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }

        binding.buttonStart.setOnClickListener {
            viewModel.startButtonClick()
            findNavController().navigate(R.id.accountOptionsFragment)
        }
    }
}