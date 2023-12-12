package com.sentraboga.buybay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentraboga.buybay.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sentraboga.buybay.data.CartProduct
import com.sentraboga.buybay.firebase.FirebaseCommon
import com.sentraboga.buybay.helper.getProductPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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

    val productsPrice = cartProducts.map {
        when(it){
            is Resource.Success -> {
                calculatePrice(it.data!!)
            } else -> null
        }
    }

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    fun deleteProdCart(cartProduct: CartProduct){
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1){
            val documentId = cartProductDocs[index].id
            firestore.collection("user").document(auth.uid!!)
                .collection("cart").document(documentId)
                .delete()
        }
    }

    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage
                .getProductPrice(cartProduct.product.price)*cartProduct.quantity)
                .toDouble()
        }.toFloat()
    }

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
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE ->{
                    if (cartProduct.quantity == 1){
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
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