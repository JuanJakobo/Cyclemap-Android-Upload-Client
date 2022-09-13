package com.example.cyclemap_android_upload_client

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import com.garmin.fit.*

import java.io.IOException
import java.io.InputStream
import java.util.*

import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filename = "Activity.fit"
        Log.i("fitFile", "Decoding file...")
        val decode = Decode()
        val messBroadcaster = MesgBroadcaster(decode)
        val listener = Listener()
        val inputStream : InputStream

        try {
            inputStream = assets.open(filename)
        } catch (e: IOException) {
            throw RuntimeException("Error opening file $filename")
        }

        messBroadcaster.addListener(listener as RecordMesgListener)

        try {
            decode.read(inputStream, messBroadcaster, messBroadcaster)
        } catch (e: FitRuntimeException) {
            if (decode.invalidFileDataSize) {
                decode.nextFile()
                decode.read(inputStream, messBroadcaster, messBroadcaster)
            } else {
                Log.e("FitFile","Exception decoding file: ")
                try {
                    inputStream.close()
                } catch (f: IOException) {
                    throw RuntimeException(f)
                }
                return
            }
        }

        try {
            inputStream.close()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        Log.i("Fit","Decoded FIT file $filename.")
    }
}

private class Listener :   RecordMesgListener {
    override fun onMesg(mesg: RecordMesg) {
        val latValue = getGeoPoint(mesg,RecordMesg.PositionLatFieldNum)
        val longValue = getGeoPoint(mesg, RecordMesg.PositionLongFieldNum)
        val altitudeValue = getGeoPoint(mesg,RecordMesg.AltitudeFieldNum)
        if(latValue != 0.0 && longValue != 0.0) {
            Log.i("FitFile", "($altitudeValue, $latValue, $longValue)")
        }
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