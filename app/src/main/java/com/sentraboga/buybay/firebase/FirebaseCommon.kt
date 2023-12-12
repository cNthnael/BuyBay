package com.sentraboga.buybay.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.sentraboga.buybay.data.CartProduct
import java.lang.Exception

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val cartCollection = firestore.collection("user").document(auth.uid!!)
        .collection("cart")

    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun updateQuantity(documentId: String, cartProduct: CartProduct, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            transition.set(documentRef, cartProduct)
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObj = document.toObject(CartProduct::class.java)
            productObj?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProdObj = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProdObj)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObj = document.toObject(CartProduct::class.java)
            productObj?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProdObj = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProdObj)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    enum class QuantityChanging {
        INCREASE, DECREASE
    }

}
