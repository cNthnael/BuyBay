package com.sentraboga.buybay.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val prodDate: String,
    val supplierName: String,
    val location: String,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val quantity: String? = null,
    val images: List<String>
): Parcelable {
    constructor(): this("0","","",0f,"","","",0f,"", "", images = emptyList())
}