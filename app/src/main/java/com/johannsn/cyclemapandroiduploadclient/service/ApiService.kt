package com.johannsn.cyclemapandroiduploadclient.service

import android.util.Log
import com.johannsn.cyclemapandroiduploadclient.service.models.Tour
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip
import okhttp3.Credentials
import okhttp3.ResponseBody
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

    fun addTour(tour: Tour, onResult: (Tour?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        retrofit.addTour(getCredentials(), tour).enqueue(
            object : Callback<Tour> {
                override fun onResponse(
                    call: Call<Tour>,
                    response: Response<Tour>
                ) {
                    if(response.code() == 201) {
                        val addedTour = response.body()
                        onResult(addedTour)
                    }
                    //TODO add auth error to all functions!, return response code and message
                    else if(response.code() == 401){
                        onResult(null)
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

    fun updateTour(tour: Tour, onResult: (Tour?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        retrofit.updateTour(getCredentials(),tour.id,tour).enqueue(
            object : Callback<Tour> {
                override fun onResponse(call: Call<Tour>, response: Response<Tour>) {
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
            }
        )
    }

    fun deleteTour(tourId: Long, onResult: (Boolean) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        retrofit.deleteTour(getCredentials(),tourId).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 204)
                        onResult(true)
                    else
                        onResult(false)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.message?.let { Log.e("json", it) }
                    onResult(false)
                }
            }
        )
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
                                distance = item.distance,
                                ascent = item.ascent,
                                descent = item.descent,
                                coordinates = item.coordinates))
                    }

                    onResult(arr)
                }
                else{
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<MutableList<Trip>>, t: Throwable) {
                //TODO return to user...
                t.message?.let { Log.e("json", it) }
                onResult(null)
            }
        })
    }

    fun addTrip(tourId: Long, trip: Trip, onResult: (Trip?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        retrofit.addTrip(getCredentials(), tourId, trip).enqueue(
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
                        //TODO add return what went wrong
                        onResult(null)
                    }
                }
                override fun onFailure(call: Call<Trip>, t: Throwable) {
                    t.message?.let { Log.e("json", it) }
                    onResult(null)
                }
            })
    }


    fun updateTrip(trip: Trip, onResult: (Trip?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        //TODO change...
        retrofit.updateTrip(getCredentials(), trip.tour?.id ?: 0,trip.id,trip).enqueue(
            object : Callback<Trip> {
                override fun onResponse(call: Call<Trip>, response: Response<Trip>) {
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
            }
        )
    }

    fun deleteTrip(trip: Trip, onResult: (Boolean) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
        //TODO change...
        retrofit.deleteTrip(getCredentials(), trip.tour?.id ?: 0,trip.id).enqueue(
            object : Callback<Trip> {
                override fun onResponse(call: Call<Trip>, response: Response<Trip>) {
                    if(response.code() == 204)
                        onResult(true)
                    else
                        onResult(false)
                }

                override fun onFailure(call: Call<Trip>, t: Throwable) {
                    t.message?.let { Log.e("json", it) }
                    onResult(false)
                }
            }
        )
    }
}