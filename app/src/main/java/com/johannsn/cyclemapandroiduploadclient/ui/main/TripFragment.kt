package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.garmin.fit.*
import com.johannsn.cyclemapandroiduploadclient.R
import com.johannsn.cyclemapandroiduploadclient.databinding.FragmentTripBinding
import com.johannsn.cyclemapandroiduploadclient.service.ApiService
import com.johannsn.cyclemapandroiduploadclient.service.models.Coordinates
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.math.pow


var geoPoints = Vector<GeoPoint>()

class TripFragment : Fragment() {
    private var _binding: FragmentTripBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        val tourId = arguments?.getLong("TourId")
        if(tourId != null) {

            //binding.titleView.text = "a"
            //binding.textView.text = "text"
            binding.saveButton.setOnClickListener {

                val apiService = ApiService()
                val trip = Trip(title="test2", text = "test")
                //add also tour to trip and not tripId, and then get tourId from the tour inside
                apiService.addTrip(tourId, trip) { trip ->
                    //var barText = "Failed to create user."
                    if (trip != null) {
                        Log.i("json", trip.id.toString())
                        //tours.add(tour)
                        //adapter.notifyDataSetChanged()
                        //barText = "Added Tour ${tour.id}"
                    }
                    //Snackbar.make(view,barText, Snackbar.LENGTH_LONG)
                    //.setAction("action",null).show()
                }
                findNavController().navigate(R.id.action_EditTripFragment_to_TripFragment)
            }
         */



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


        //TODO make Coordinates
        val coordinates = mutableListOf<Coordinates>()
        for(geopoint in geoPoints)
            coordinates.add(Coordinates(geopoint.longitude,geopoint.latitude,geopoint.altitude))
        binding.buttonSave.setOnClickListener {

            /*
            val apiService = ApiService()

            //TOOD catch
            //TODO extend api so addTrip also adds Coordinates
            val test = arguments?.getLong("TripId")!!
            Log.i("json",test.toString())

            apiService.addCoordinates(arguments?.getLong("TripId")!!,coordinates) {
                if(it != null)
                {
                    Log.i("json","sucess")
                }
            }
            */
            //save coordinations to trip

            findNavController().navigate(R.id.action_TripFragment_to_TripsFragment)
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