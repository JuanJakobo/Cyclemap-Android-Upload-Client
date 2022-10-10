package com.johannsn.cyclemapandroiduploadclient.service

import android.util.Log
import com.garmin.fit.*
import com.johannsn.cyclemapandroiduploadclient.service.models.Coordinates
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip
import java.io.IOException
import java.io.InputStream
import kotlin.math.pow
import kotlin.math.roundToInt


var trip: Trip? = null

class FitToGeo {

    fun readInFit(open: InputStream): Trip {

        val decode = Decode()
        val messBroadcaster = MesgBroadcaster(decode)
        val listener = Listener()
        trip = Trip()

        messBroadcaster.addListener(listener as RecordMesgListener)

        try {
            decode.read(open, messBroadcaster, messBroadcaster)
        } catch (e: FitRuntimeException) {
            if (decode.invalidFileDataSize) {
                decode.nextFile()
                decode.read(open, messBroadcaster, messBroadcaster)
            } else {
                Log.e("FitFile", "Exception decoding file: ")
                try {
                    open.close()
                } catch (f: IOException) {
                    throw RuntimeException(f)
                }
            }
        }

        try {
            open.close()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        return trip as Trip
    }

    private class Listener : RecordMesgListener {
        var counter = 0 //TODO test if is really 0
        var departer = 15

        override fun onMesg(mesg: RecordMesg) {

            if(counter % departer == 0) {
                //write descent and ascent
                getDeveloperValue(mesg)
                trip?.distance = getValue(mesg, RecordMesg.DistanceFieldNum).roundToInt()
                val latValue = getValue(mesg, RecordMesg.PositionLatFieldNum)
                val lngValue = getValue(mesg, RecordMesg.PositionLongFieldNum)
                if (latValue != 0.0 && lngValue != 0.0) {
                    trip?.coordinates?.add(Coordinates(lngValue,latValue))
                }
            }
            counter++
        }

        private fun getDeveloperValue(mesg: Mesg) {
            for (developerField in mesg.developerFields) {
                if (developerField.numValues < 1) continue
                if (developerField.isDefined) {
                    when (developerField.name) {
                        "descent"-> trip?.descent = developerField.getDoubleValue(0).roundToInt()
                        "ascent"-> trip?.ascent = developerField.getDoubleValue(0).roundToInt()
                    }
                }
            }
        }

        private fun getValue(mesg: RecordMesg, fieldID: Int): Double {
            val fields = mesg.getOverrideField(fieldID.toShort())
            val profileField = Factory.createField(mesg.num, fieldID) ?: return 0.0
            for (field in fields) {
                return if (profileField.name == "position_lat" || profileField.name == "position_long")
                    (field.value as Int).toDouble() * (180 / 2.0.pow(31.0))
                else
                    field.value as Double
            }
            return 0.0
        }
    }
}