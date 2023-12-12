package com.sentraboga.buybay.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.sentraboga.buybay.data.Category
import com.sentraboga.buybay.util.Resource
import com.sentraboga.buybay.viewmodel.CategoryViewModel
import com.sentraboga.buybay.viewmodel.factory.BaseCategoryViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FreshFragment: BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Fresh)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when (it){
                    is Resource.Loading ->{
                        showOfferLoad()
                    }
                    is Resource.Success ->{
                        offerApd.differ.submitList(it.data)
                        hideOfferLoad()
                    }
                    is Resource.Error ->{
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                        hideOfferLoad()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.baseProducts.collectLatest {
                when (it){
                    is Resource.Loading ->{
                        showBaseLoad()
                    }
                    is Resource.Success ->{
                        baseProductAdp.differ.submitList(it.data)
                        hideBaseLoad()
                    }
                    is Resource.Error ->{
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                        hideBaseLoad()
                    }
                    else -> Unit
                }
            }
        }

    }
}