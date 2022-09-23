package com.johannsn.cyclemapandroiduploadclient.service

import android.util.Log
import com.johannsn.cyclemapandroiduploadclient.service.models.Coordinates
import com.johannsn.cyclemapandroiduploadclient.service.models.Tour
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiService {

    fun getTours(onResult: (MutableList<Tour>?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        val call: Call<MutableList<Tour>> = retrofit.getTours()
        call.enqueue(object : Callback<MutableList<Tour>> {
            override fun onResponse(
                call: Call<MutableList<Tour>>,
                response: Response<MutableList<Tour>>
            ) {
                if(response.code() == 200) {
                    val res = response.body()
                    val arr = mutableListOf<Tour>()
                    if (res != null) {
                        for(item in res)
                            arr.add(Tour(item.id, item.title))
                    }

                    onResult(arr)
                }
                else{
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<MutableList<Tour>>, t: Throwable) {
                t.message?.let { Log.e("json", it) }
                onResult(null)
            }
        })
    }

    fun getTripsForTour(tourId: Long, onResult: (MutableList<Trip>?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        val call: Call<MutableList<Trip>> = retrofit.getTripsForTour(tourId)
        call.enqueue(object : Callback<MutableList<Trip>> {
            override fun onResponse(
                call: Call<MutableList<Trip>>,
                response: Response<MutableList<Trip>>
            ) {
                if(response.code() == 200) {
                    val res = response.body()
                    val arr = mutableListOf<Trip>()
                    if (res != null) {
                        for(item in res)
                            arr.add(Trip(id = item.id,
                                title = item.title,
                                text = item.text,
                                tour = item.tour,
                                coordinates = item.coordinates))
                    }

                    onResult(arr)
                }
                else{
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<MutableList<Trip>>, t: Throwable) {
                t.message?.let { Log.e("json", it) }
                onResult(null)
            }
        })
    }

    fun addTrip(tourId: Long, trip: Trip, onResult: (Trip?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        retrofit.addTrip(tourId, trip).enqueue(
            object : Callback<Trip> {
                override fun onResponse(
                    call: Call<Trip>,
                    response: Response<Trip>
                ) {
                    if(response.code() == 201) {
                        val addedTrip = response.body()
                        onResult(addedTrip)
                    }
                    else{
                        onResult(null)
                    }
                }
                override fun onFailure(call: Call<Trip>, t: Throwable) {
                    t.message?.let { Log.e("json", it) }
                    onResult(null)
                }
            })
    }

    fun addTour(tour: Tour, onResult: (Tour?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        retrofit.addTour(tour).enqueue(
            object : Callback<Tour> {
                override fun onResponse(
                    call: Call<Tour>,
                    response: Response<Tour>
                ) {
                    if(response.code() == 201) {
                        val addedTour = response.body()
                        onResult(addedTour)
                    }
                    else{
                        onResult(null)
                    }
                }
                override fun onFailure(call: Call<Tour>, t: Throwable) {
                    t.message?.let { Log.e("json", it) }
                    onResult(null)
                }
            })
    }

    fun addCoordinates(tripId: Long, coordinates: List<Coordinates>, onResult: (Coordinates?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        retrofit.addCoordinates(tripId, coordinates).enqueue(
            object : Callback<Coordinates> {
                override fun onResponse(
                    call: Call<Coordinates>,
                    response: Response<Coordinates>
                ) {
                    if(response.code() == 201) {
                        val addedCoordinates = response.body()
                        onResult(addedCoordinates)
                    }
                    else{
                        onResult(null)
                    }
                }
                override fun onFailure(call: Call<Coordinates>, t: Throwable) {
                    t.message?.let { Log.e("json", it) }
                    onResult(null)
                }
            })
    }
}