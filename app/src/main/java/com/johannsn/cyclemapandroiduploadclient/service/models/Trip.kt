package com.johannsn.cyclemapandroiduploadclient.service.models

data class Trip(
    val id: Long = 0,
    val text: String,
    var tour: Tour? = null,
    val coordinates: MutableList<Coordinates>? = mutableListOf(),
)
