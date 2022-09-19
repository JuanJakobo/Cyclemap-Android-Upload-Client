package com.johannsn.cyclemapandroiduploadclient.service

import com.johannsn.cyclemapandroiduploadclient.service.models.Tour
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android")
    @GET("tours/{id}")
    fun getTour(
        @Path("id") id: Long): Call<Tour>

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
    @GET("/tours/{tourId}/trips")
    fun getTripsForTour(@Path("tourId") tourId: Long): Call<MutableList<Trip>>

    }