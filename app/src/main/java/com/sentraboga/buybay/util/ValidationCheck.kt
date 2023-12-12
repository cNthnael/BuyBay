package com.sentraboga.buybay.util

import android.util.Patterns

fun validatePhone(phone: String): RegisterValidation{
    if (phone.isEmpty()){
        return RegisterValidation.Failed("Nomor Telepon tidak boleh kosong!")
    }

    if(phone.length < 6){
        return RegisterValidation.Failed("Nomor tidak valid!")
    }

    return RegisterValidation.Success
}

fun validateEmail(email: String): RegisterValidation{
    if (email.isEmpty())
        return RegisterValidation.Failed("Email tidak boleh kosong!")
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Format Email tidak valid!")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation{
    if (password.isEmpty())
        return RegisterValidation.Failed("Kata Sandi tidak boleh kosong!")
    if (password.length < 6)
        return RegisterValidation.Failed("Kata Sandi harus lebih dari 6 karakter!")

    return RegisterValidation.Success
}