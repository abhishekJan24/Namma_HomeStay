package com.namma.homestay.data.model

data class HomeProfile(
    val homeName: String = "",
    val location: String = "",
    val description: String = "",
    val pricePerNight: String = "",
    val cleanRoom: Boolean = false,
    val cleanToilet: Boolean = false,
    val safeDrinkingWater: Boolean = false,
    val foodAvailable: Boolean = false,
    val roomImageUrl: String = "",
    val toiletImageUrl: String = "",
    val farmImageUrl: String = ""
)
