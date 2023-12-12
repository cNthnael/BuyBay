package com.sentraboga.buybay.data

data class CartProduct(

    val product: Product,
    val quantity: Int,
    val subsDuration: String? = null,
){
    constructor(): this(Product(), 1, null)
}
