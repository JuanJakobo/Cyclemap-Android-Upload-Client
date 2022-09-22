package com.johannsn.cyclemapandroiduploadclient.service

import android.util.Log
import com.garmin.fit.*
import com.johannsn.cyclemapandroiduploadclient.service.models.Coordinates
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.math.log
import kotlin.math.pow

val coordinates = mutableListOf<Coordinates>()
var counter = 0
var departer = 3

class fitToGeo {

    fun readInFit(open: InputStream): MutableList<Coordinates> {

        val decode = Decode()
        val messBroadcaster = MesgBroadcaster(decode)
        val listener = Listener()
        val inputStream = open
        /*
        try {
            inputStream = open
        } catch (e: IOException) {
            throw RuntimeException("Error opening file ")
        }
         */

        counter = 0
        messBroadcaster.addListener(listener as RecordMesgListener)

        try {
            decode.read(inputStream, messBroadcaster, messBroadcaster)
        } catch (e: FitRuntimeException) {
            if (decode.invalidFileDataSize) {
                decode.nextFile()
                decode.read(inputStream, messBroadcaster, messBroadcaster)
            } else {
                Log.e("FitFile", "Exception decoding file: ")
                try {
                    inputStream.close()
                } catch (f: IOException) {
                    throw RuntimeException(f)
                }
                //TODO return error //throw?
                //return
            }
        }

        try {
            inputStream.close()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        //Log.i("Fit", "Decoded FIT file $filename.")
        Log.i("Fit", "Decoded FIT file.")
        return coordinates
    }

    //private vs internal?
    private class Listener : RecordMesgListener {
        override fun onMesg(mesg: RecordMesg) {
            //TODO read distance, meters
            Log.i("fitTo",RecordMesg.DistanceFieldNum.toString())
            Log.i("fitTo",RecordMesg.AltitudeFieldNum.toString())
            if(counter % departer == 0) {
                val latValue = getGeoPoint(mesg, RecordMesg.PositionLatFieldNum)
                val lngValue = getGeoPoint(mesg, RecordMesg.PositionLongFieldNum)
                val altitudeValue = getGeoPoint(mesg, RecordMesg.AltitudeFieldNum)
                if (latValue != 0.0 && lngValue != 0.0) {
                    //TODO add altitude
                    coordinates.add(Coordinates(lngValue,latValue))
                }
            }
            counter++
        }

        private fun getGeoPoint(mess: RecordMesg, fieldID: Int): Double {
            val fields = mess.getOverrideField(fieldID.toShort())
            val profileField = Factory.createField(mess.num, fieldID) ?: return 0.0
            for (field in fields) {
                if (profileField.name == "altitude")
                    return (field.value as Double).toDouble()
                else if (profileField.name == "position_lat" || profileField.name == "position_long")
                    return (field.value as Int).toDouble() * (180 / 2.0.pow(31.0))
            }
            return 0.0
        }
    }
}