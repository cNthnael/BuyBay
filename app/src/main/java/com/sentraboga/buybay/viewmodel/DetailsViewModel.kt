package com.sentraboga.buybay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sentraboga.buybay.data.CartProduct
import com.sentraboga.buybay.firebase.FirebaseCommon
import com.sentraboga.buybay.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {
    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun updateProduct(cartProduct: CartProduct, newQuantity: Int, newSubsDuration: String) {
        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }

        firestore.collection("user").document(auth.uid!!)
            .collection("cart").whereEqualTo("product.id", cartProduct.product.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Add new product
                    addNewProd(cartProduct, newQuantity, newSubsDuration)
                } else {
                    val document = querySnapshot.documents[0]
                    val product = document.toObject(CartProduct::class.java)
                    if (product != null) {
                        // Update existing product with new quantity and order duration
                        val updatedProduct = product.copy(quantity = newQuantity, subsDuration = newSubsDuration)
                        updateQuantity(document.id, updatedProduct)
                    }
                }
            }
            .addOnFailureListener { e ->
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
    }

    private fun addNewProd(cartProduct: CartProduct, quantity: Int, subsDuration: String) {
        val newCartProduct = cartProduct.copy(quantity = quantity, subsDuration = subsDuration)
        firebaseCommon.addProductToCart(newCartProduct) { addedProd, e ->
            viewModelScope.launch {
                if (e == null) {
                    _addToCart.emit(Resource.Success(addedProd!!))
                } else {
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun updateQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.updateQuantity(documentId, cartProduct) { _, e ->
            viewModelScope.launch {
                if (e == null) {
                    _addToCart.emit(Resource.Success(cartProduct))
                } else {
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }


}