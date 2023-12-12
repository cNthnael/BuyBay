package com.sentraboga.buybay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.sentraboga.buybay.data.Category
import com.sentraboga.buybay.data.Product
import com.sentraboga.buybay.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category

): ViewModel() {

    private val pagingInfo = PagingInfo()
    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _baseProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val baseProducts = _baseProducts.asStateFlow()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts(){
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", category.category)
            .whereNotEqualTo("offerPercentage", null)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts(){
        viewModelScope.launch {
            _baseProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", category.category)
            .whereEqualTo("offerPercentage", null)
            .limit(pagingInfo.prodBestPage * 6)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _baseProducts.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _baseProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}

