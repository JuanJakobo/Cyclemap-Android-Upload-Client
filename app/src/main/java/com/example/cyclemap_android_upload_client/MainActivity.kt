package com.example.cyclemap_android_upload_client

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.garmin.fit.*

import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

import java.io.IOException
import java.io.InputStream
import java.util.*

import kotlin.math.pow

var geoPoints = Vector<GeoPoint>()

class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

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

        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        val trip = Polyline(map)
        if(!geoPoints.isEmpty()) {
            for (point in geoPoints)
                trip.addPoint(point)
            map.overlays.add(trip)
            mapController.setCenter(geoPoints.elementAt(geoPoints.size / 2))
        }
        mapController.setZoom(11.1)
    }

    //https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Kotlin)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }
}

private class Listener :   RecordMesgListener {
    override fun onMesg(mesg: RecordMesg) {
        val latValue = getGeoPoint(mesg,RecordMesg.PositionLatFieldNum)
        val longValue = getGeoPoint(mesg, RecordMesg.PositionLongFieldNum)
        val altitudeValue = getGeoPoint(mesg,RecordMesg.AltitudeFieldNum)
        if(latValue != 0.0 && longValue != 0.0) {
            Log.i("FitFile", "($altitudeValue, $latValue, $longValue)")
            geoPoints.addElement(GeoPoint(latValue, longValue))
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