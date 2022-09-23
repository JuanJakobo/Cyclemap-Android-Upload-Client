package com.johannsn.cyclemapandroiduploadclient.service.models

data class Trip(
    val id: Long = 0,
    val title: String,
    val text: String,
    var tour: Tour? = null,
    val coordinates: MutableList<Coordinates>? = mutableListOf(),
){
    override fun toString(): String {
        return title
    }
}
