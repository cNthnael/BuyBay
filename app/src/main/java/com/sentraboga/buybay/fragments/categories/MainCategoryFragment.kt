package com.sentraboga.buybay.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sentraboga.buybay.R
import com.sentraboga.buybay.adapters.BestDealAdp
import com.sentraboga.buybay.adapters.BestProductAdp
import com.sentraboga.buybay.adapters.SpecialProductsAdp
import com.sentraboga.buybay.databinding.FragmentMainCategoryBinding
import com.sentraboga.buybay.util.Resource
import com.sentraboga.buybay.util.showBottomNavView
import com.sentraboga.buybay.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProdAdp: SpecialProductsAdp
    private lateinit var bestDealAdp: BestDealAdp
    private lateinit var bestProductAdp: BestProductAdp
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductRv()
        setupBestDealRv()
        setupBestProductRv()

        specialProdAdp.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, b)
        }

        bestDealAdp.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, b)
        }

        bestProductAdp.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, b)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when (it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        specialProdAdp.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealProducts.collectLatest {
                when (it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestDealAdp.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProduct.collectLatest {
                when (it){
                    is Resource.Loading -> {
                       binding.fetchProgBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bestProductAdp.differ.submitList(it.data)
                        binding.fetchProgBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.fetchProgBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        binding.nestedScrollMainCat.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,_,scrollY,_,_ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.fetchBestProducts()
            }
        })

    }

    private fun setupSpecialProductRv() {
        specialProdAdp = SpecialProductsAdp()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProdAdp
        }
    }

    private fun setupBestProductRv() {
        bestProductAdp = BestProductAdp()
        binding.rvBaseProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdp
        }
    }

    private fun setupBestDealRv() {
        bestDealAdp = BestDealAdp()
        binding.rvBestDeal.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealAdp
        }
    }

    override fun onResume() {
        super.onResume()

        showBottomNavView()
    }

    private fun hideLoading() {
        binding.mainCatProgBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCatProgBar.visibility = View.VISIBLE
    }

}