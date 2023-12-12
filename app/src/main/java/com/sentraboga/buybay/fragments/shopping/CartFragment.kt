package com.sentraboga.buybay.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.sentraboga.buybay.R
import com.sentraboga.buybay.adapters.CartProductAdp
import com.sentraboga.buybay.databinding.CartProductItemBinding
import com.sentraboga.buybay.databinding.FragmentCartBinding
import com.sentraboga.buybay.util.Resource
import com.sentraboga.buybay.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment: Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartProductAdp by lazy { CartProductAdp() }
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()

        binding.closeCart.setOnClickListener{
            findNavController().navigateUp()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()){
                            showEmptyCart()
                        } else {
                            hideEmptyCart()
                            cartProductAdp.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun hideEmptyCart() {
//        TODO("Not yet implemented")
    }

    private fun showEmptyCart() {
//
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartProductAdp
        }
    }

}