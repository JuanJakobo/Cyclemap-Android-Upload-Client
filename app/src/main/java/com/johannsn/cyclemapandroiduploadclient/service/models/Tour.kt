package com.johannsn.cyclemapandroiduploadclient.service.models

import com.google.gson.annotations.SerializedName

data class Tour(
    @SerializedName("id")
    val id: Long? = null,
    val title: String,
    //val trips: MutableList<Trip>? = mutableListOf(),
)
