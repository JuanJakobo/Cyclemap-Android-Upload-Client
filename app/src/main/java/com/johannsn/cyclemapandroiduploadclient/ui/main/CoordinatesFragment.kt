package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.garmin.fit.*
import com.google.gson.Gson
import com.johannsn.cyclemapandroiduploadclient.R
import com.johannsn.cyclemapandroiduploadclient.databinding.FragmentCoordinatesBinding
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.math.pow


var geoPoints = Vector<GeoPoint>()

class CoordinatesFragment : Fragment() {
    private var _binding: FragmentCoordinatesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoordinatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(geoPoints.isEmpty()) {
            val filename = "Activity.fit"
            readInFit(filename)
        }
        val map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        val trip = Polyline(map)
        if (!geoPoints.isEmpty()) {
            for (point in geoPoints)
                trip.addPoint(point)
            map.overlays.add(trip)
            mapController.setCenter(geoPoints.elementAt(geoPoints.size / 2))
        }
        mapController.setZoom(11.1)

        binding.textView.text = "this is it"

        val gson = Gson()
        val geoPointJson: String = gson.toJson(geoPoints)
        Log.i("Json", geoPointJson)
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_CoordinatesFragment_to_TourFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun readInFit(filename: String) {

        Log.i("fitFile", "Decoding file...")
        val decode = Decode()
        val messBroadcaster = MesgBroadcaster(decode)
        val listener = Listener()
        val inputStream: InputStream

        try {
            inputStream = context?.assets?.open(filename)!!
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
                Log.e("FitFile", "Exception decoding file: ")
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

        Log.i("Fit", "Decoded FIT file $filename.")
    }
    //private vs internal?
    private class Listener : RecordMesgListener {
        override fun onMesg(mesg: RecordMesg) {
            val latValue = getGeoPoint(mesg, RecordMesg.PositionLatFieldNum)
            val longValue = getGeoPoint(mesg, RecordMesg.PositionLongFieldNum)
            val altitudeValue = getGeoPoint(mesg, RecordMesg.AltitudeFieldNum)
            if (latValue != 0.0 && longValue != 0.0) {
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
}