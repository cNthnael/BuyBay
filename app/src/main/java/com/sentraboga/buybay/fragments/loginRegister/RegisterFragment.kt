package com.sentraboga.buybay.fragments.loginRegister

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sentraboga.buybay.R
import com.sentraboga.buybay.data.User
import com.sentraboga.buybay.databinding.FragmentRegisterBinding
import com.sentraboga.buybay.util.RegisterValidation
import com.sentraboga.buybay.util.Resource
import com.sentraboga.buybay.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment: Fragment() {


    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAlreadyAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment2)
        }

        binding.apply {
            buttonRegister.setOnClickListener {
                val phone = edPhone.text.toString().trim()
                val user = User(
                    edUsername.text.toString().trim(),
                    edPhone.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()
                viewModel.createAccountEmPass(phone, user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when(it){
                    is Resource.Loading -> {
                        binding.buttonRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("test", it.data.toString())
                        binding.buttonRegister.revertAnimation()
                    }
                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        binding.buttonRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect{
                validation ->
                if (validation.phone is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edPhone.apply {
                            requestFocus()
                            error = validation.phone.message
                        }
                    }
                }

                if (validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}