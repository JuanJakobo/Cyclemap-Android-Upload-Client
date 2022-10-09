package com.johannsn.cyclemapandroiduploadclient.service.models

data class Trip(
    val id: Long = 0,
    val title: String = "",
    val text: String = "",
    var tour: Tour? = null,
    var distance: Double = 0.0,
    var descent: Double = 0.0,
    var ascent: Double = 0.0,
    val coordinates: MutableList<Coordinates> = mutableListOf(),
){
    override fun toString(): String {
        return "$title (Distance $distance m)"
    }
}
