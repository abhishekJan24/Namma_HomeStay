package com.namma.homestay.data.model

data class DailyMenu(
    val id: String = "",
    val itemName: String = "",
    val price: String = "",
    val addedAt: Long = System.currentTimeMillis()
)
