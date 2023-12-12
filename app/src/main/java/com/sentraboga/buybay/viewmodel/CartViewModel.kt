package com.sentraboga.buybay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentraboga.buybay.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sentraboga.buybay.data.CartProduct
import com.sentraboga.buybay.firebase.FirebaseCommon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {
    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()
    private var cartProductDocs = emptyList<DocumentSnapshot>()
    init {
        getCartProduct()
    }

    private fun getCartProduct() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }

        firestore.collection("user").document(auth.uid!!)
            .collection("cart")
            .addSnapshotListener{ value, error ->
                if (error != null || value == null){
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    cartProductDocs = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Success(cartProducts))
                    }
                }
            }

    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ){

        val index = cartProducts.value.data?.indexOf(cartProduct)


        if (index != null && index != -1){
            val documentId = cartProductDocs[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE ->{
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE ->{
                    decreaseQuantity(documentId)
                }
            }
        }

    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){ result, e ->
            if (e != null){
                viewModelScope.launch { _cartProducts.emit(Resource.Error(e.message.toString())) }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){ result, e ->
            if (e != null){
                viewModelScope.launch { _cartProducts.emit(Resource.Error(e.message.toString())) }
            }
        }
    }

}