package com.sentraboga.buybay.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sentraboga.buybay.R
import com.sentraboga.buybay.adapters.BestProductAdp
import com.sentraboga.buybay.databinding.FragmentBaseCategoryBinding
import com.sentraboga.buybay.util.showBottomNavView

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {

    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerApd: BestProductAdp by lazy { BestProductAdp() }
    protected val baseProductAdp: BestProductAdp by lazy { BestProductAdp() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv()
        setupBestProdRv()

        baseProductAdp.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, b)
        }

        offerApd.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, b)
        }

        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dx != 0){
                    onOfferPagingReq()
                }
            }
        })

        binding.nestedScrollBaseCat.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                onBestProductsPagingReq()
            }
        })

    }

    fun showOfferLoad(){
        binding.offerProdProgBar.visibility = View.VISIBLE
    }

    fun hideOfferLoad(){
        binding.offerProdProgBar.visibility = View.GONE
    }

    fun showBaseLoad(){
        binding.baseProdProgBar.visibility = View.VISIBLE
    }

    fun hideBaseLoad(){
        binding.baseProdProgBar.visibility = View.GONE
    }

    open fun onOfferPagingReq(){

    }

    open fun onBestProductsPagingReq(){

    }

    private fun setupBestProdRv() {
        binding.rvBaseProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = baseProductAdp
        }
    }

    private fun setupOfferRv() {
        binding.rvOffer.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerApd
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavView()
    }

}