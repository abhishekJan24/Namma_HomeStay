package com.namma.homestay.data.model

data class Inquiry(
    val id: String = "",
    val name: String = "",
    val message: String = "",
    val phone: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
