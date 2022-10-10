package com.johannsn.cyclemapandroiduploadclient.service.models

data class Trip(
    val id: Long = 0,
    val title: String = "",
    val text: String = "",
    var tour: Tour? = null,
    var distance: Int = 0,
    var descent: Int = 0,
    var ascent: Int = 0,
    val coordinates: MutableList<Coordinates> = mutableListOf(),
){
    override fun toString(): String {
        return "$title (Distance $distance m)"
    }

    private fun add(trip: Trip){
        this.distance += trip.distance
        this.descent += trip.descent
        this.ascent += trip.ascent
    }

    fun addAtEnd(trip: Trip){
        add(trip)
        this.coordinates.addAll(trip.coordinates)
    }

    fun addAtBegin(trip: Trip){
        add(trip)
        trip.coordinates.addAll(this.coordinates)
        this.coordinates.clear()
        this.coordinates.addAll(trip.coordinates)
    }
}
