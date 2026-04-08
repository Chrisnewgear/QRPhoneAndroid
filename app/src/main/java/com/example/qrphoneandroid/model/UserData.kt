package com.example.qrphoneandroid.model

data class UserData(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String? = null
)
