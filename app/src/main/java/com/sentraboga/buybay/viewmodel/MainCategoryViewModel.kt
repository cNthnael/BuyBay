package com.sentraboga.buybay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sentraboga.buybay.data.Product
import com.sentraboga.buybay.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {
    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealProducts: StateFlow<Resource<List<Product>>> = _bestDealProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDealProducts()
        fetchBestProducts()
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
                firestore.collection("Products")
                    .orderBy("id", Query.Direction.ASCENDING)
                    .limit(pagingInfo.prodBestPage * 6)
                    .get()
                    .addOnSuccessListener { result ->
                        val bestProducts = result.toObjects(Product::class.java)
                        pagingInfo.isPagingEnd = bestProduct == pagingInfo.oldBestPage
                        pagingInfo.oldBestPage = bestProducts
                        viewModelScope.launch {
                            _bestProducts.emit(Resource.Success(bestProducts))
                        }
                        pagingInfo.prodBestPage++
                    }.addOnFailureListener {
                        viewModelScope.launch {
                            _bestProducts.emit(Resource.Error(it.message.toString()))
                        }
                    }
            }
        }
    }

    fun fetchBestDealProducts() {
        viewModelScope.launch {
            _bestDealProducts.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("location", "Jakarta Timur")
            .get().addOnSuccessListener { result ->
                val bestDealProducts = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealProducts.emit(Resource.Success(bestDealProducts))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }

        firestore.collection("Products")
            .whereGreaterThan("offerPercentage", 0.20)
            .get().addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}

internal data class PagingInfo(
    var prodBestPage: Long = 1,
    var oldBestPage: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)