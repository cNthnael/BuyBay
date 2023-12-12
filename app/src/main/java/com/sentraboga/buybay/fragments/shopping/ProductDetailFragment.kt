package com.sentraboga.buybay.fragments.shopping

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sentraboga.buybay.R
import com.sentraboga.buybay.adapters.ViewPager2ImagesAdp
import com.sentraboga.buybay.data.CartProduct
import com.sentraboga.buybay.databinding.FragmentProductDetailBinding
import com.sentraboga.buybay.util.Resource
import com.sentraboga.buybay.util.hideBottomNavView
import com.sentraboga.buybay.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class ProductDetailFragment: Fragment() {
    private val args by navArgs<ProductDetailFragmentArgs>()
    private lateinit var binding: FragmentProductDetailBinding
    private val viewPagerAdapter by lazy { ViewPager2ImagesAdp() }
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavView()
        binding = FragmentProductDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupViewPager()

        binding.imgClose.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.buttonAddtoCart.setOnClickListener {
            val selectedQuantity = binding.spinnerQuantity.selectedItem.toString().toInt()
            val selectedOrderDuration = binding.spinnerOrderDuration.selectedItem.toString()

            viewModel.updateProduct(
                CartProduct(product, selectedQuantity, selectedOrderDuration),
                selectedQuantity,
                selectedOrderDuration
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.buttonAddtoCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonAddtoCart.revertAnimation()
                        Toast.makeText(requireContext(), "Berhasil menambahkan ke tas!", Toast.LENGTH_SHORT).show()
                        binding.buttonAddtoCart.setTextColor(resources.getColor(R.color.g_white))
                        binding.buttonAddtoCart.setBackgroundColor(resources.getColor(R.color.g_black))
                    }
                    is Resource.Error -> {
                        binding.buttonAddtoCart.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            tvProdName.text = product.name
            tvProdLocationInfo.text = product.location
            tvProdSizeInfo.text = product.quantity
            tvProdDate.text = product.prodDate
            tvProdDesc.text = product.description

            if (product.offerPercentage != null) {
                val remainingPricePercentage = 1f - product.offerPercentage
                val priceAfterOffer = remainingPricePercentage * product.price
                val formattedPriceAfterOff = NumberFormat.getNumberInstance(Locale.US).format(priceAfterOffer)
                tvProdNewPrice.text = "Rp $formattedPriceAfterOff"

                val formattedOldPrice = NumberFormat.getNumberInstance(Locale.US).format(product.price)
                tvProdPriceOld.text = "Rp $formattedOldPrice"
                tvProdPriceOld.paintFlags = tvProdPriceOld.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvProdPriceOld.visibility = View.VISIBLE
            } else {
                val formattedOldPrice = NumberFormat.getNumberInstance(Locale.US).format(product.price)
                tvProdNewPrice.text = "Rp $formattedOldPrice"
                tvProdNewPrice.setTextColor(ContextCompat.getColor(tvProdNewPrice.context, R.color.g_gray700))
                tvProdNewPrice.visibility = View.VISIBLE
                tvProdPriceOld.visibility = View.GONE
            }

        }


        viewPagerAdapter.differ.submitList(product.images)
    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerProdImg.adapter = viewPagerAdapter
        }
    }

}