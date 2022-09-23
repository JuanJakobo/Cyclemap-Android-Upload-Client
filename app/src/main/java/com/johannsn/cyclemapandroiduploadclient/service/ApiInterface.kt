package com.johannsn.cyclemapandroiduploadclient.service

import com.johannsn.cyclemapandroiduploadclient.service.models.Coordinates
import com.johannsn.cyclemapandroiduploadclient.service.models.Tour
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android")
    @GET("tours")
    fun getTours(): Call<MutableList<Tour>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android")
    @POST("tours")
    fun addTour(@Body response: Tour): Call<Tour>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android")
    @POST("trips/{tripId}/coordinates")
    fun addCoordinates(@Path("tripId") tripId: Long, @Body response: List<Coordinates>): Call<Coordinates>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android")
    @GET("tours/{tourId}/trips")
    fun getTripsForTour(@Path("tourId") tourId: Long): Call<MutableList<Trip>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android")
    @POST("tours/{tourId}/trips")
    fun addTrip(@Path("tourId") tourId: Long, @Body response: Trip): Call<Trip>

    }