package com.sentraboga.buybay.util

sealed class RegisterValidation(){
    object Success: RegisterValidation()
    data class Failed(val message: String): RegisterValidation()
}

data class RegisterFieldState(
    val phone: RegisterValidation,
    val email: RegisterValidation,
    val password: RegisterValidation
)
