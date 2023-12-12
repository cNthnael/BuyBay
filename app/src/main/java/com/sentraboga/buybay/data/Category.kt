package com.sentraboga.buybay.data

sealed class Category(val category: String){
    object Fresh: Category("Fresh")
    object Frozen: Category("Frozen")
    object Canned: Category("Canned")
}