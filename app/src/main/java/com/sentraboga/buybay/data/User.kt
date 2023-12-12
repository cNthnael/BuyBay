package com.sentraboga.buybay.data

data class User(
    val userName: String,
    val phone: String,
    val email: String,
    val imagePath: String = ""

){
    constructor(): this("","","","")
}
