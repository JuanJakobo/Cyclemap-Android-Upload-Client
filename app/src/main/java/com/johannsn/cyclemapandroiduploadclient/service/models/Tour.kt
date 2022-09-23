package com.johannsn.cyclemapandroiduploadclient.service.models

import com.google.gson.annotations.SerializedName

data class Tour(
    @SerializedName("id")
    val id: Long = 0,
    val title: String,
){
    override fun toString(): String {
        return title
    }
}
