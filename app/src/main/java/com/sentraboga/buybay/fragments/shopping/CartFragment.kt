package com.sentraboga.buybay.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.sentraboga.buybay.firebase.FirebaseCommon
import com.sentraboga.buybay.util.Resource
import com.sentraboga.buybay.util.VerticalItemDecoration
import com.sentraboga.buybay.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale

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

        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(price)
                    binding.tvTotalPrice.text = "Rp $formattedPrice"
                }
            }
        }

        cartProductAdp.onProdClick = {
            val b = Bundle().apply {
                putParcelable("product", it.product)
            }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailFragment, b)
        }

        cartProductAdp.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartProductAdp.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Hapus produk dari tas")
                    setMessage("Anda ingin hapus produk ini dari tas?")
                        .setNegativeButton("Cancel"){ dialog,_ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Ya"){ dialog,_ ->
                            dialog.dismiss()
                            viewModel.deleteProdCart(it)
                        }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

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
                            hideOtherView()
                        } else {
                            hideEmptyCart()
                            showOtherViews()
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

    private fun hideOtherView() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBox.visibility = View.GONE
            buttonConfirmCart.visibility = View.GONE
            tvHint.visibility = View.GONE
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBox.visibility = View.VISIBLE
            buttonConfirmCart.visibility = View.VISIBLE
            tvHint.visibility = View.VISIBLE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmp.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmp.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartProductAdp
            addItemDecoration(VerticalItemDecoration())
        }
    }

}