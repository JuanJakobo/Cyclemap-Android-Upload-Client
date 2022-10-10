package com.johannsn.cyclemapandroiduploadclient.service

import com.johannsn.cyclemapandroiduploadclient.service.models.Tour
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip
import okhttp3.ResponseBody
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
    fun addTour(@Header("Authorization") auth: String, @Body response: Tour): Call<Tour>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android")
    @PUT("tours/{tourId}")
    fun updateTour(@Header("Authorization") auth: String, @Path("tourId") tourId: Long, @Body response: Tour): Call<Tour>

    @Headers(
        "Accept: application/json",
        "Platform: android")
    @DELETE("tours/{tourId}")
    fun deleteTour(@Header("Authorization") auth: String, @Path("tourId") tourId: Long): Call<ResponseBody>

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
    fun addTrip(@Header("Authorization") auth: String, @Path("tourId") tourId: Long, @Body response: Trip): Call<Trip>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android")
    @PUT("tours/{tourId}/trips/{tripId}")
    fun updateTrip(@Header("Authorization") auth: String, @Path("tourId") tourId: Long, @Path("tripId") tripId: Long, @Body response: Trip): Call<Trip>

    @Headers(
        "Accept: application/json",
        "Platform: android")
    @DELETE("tours/{tourId}/trips/{tripId}")
    fun deleteTrip(@Header("Authorization") auth: String, @Path("tourId") tourId: Long, @Path("tripId") tripId: Long): Call<Trip>
}