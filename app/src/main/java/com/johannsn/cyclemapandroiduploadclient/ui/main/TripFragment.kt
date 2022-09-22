package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.johannsn.cyclemapandroiduploadclient.databinding.FragmentTripBinding
import com.johannsn.cyclemapandroiduploadclient.service.ApiService
import com.johannsn.cyclemapandroiduploadclient.service.models.Coordinates
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Polyline

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

        val map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(11.1)
        if ((activity as MainActivity?)!!.geoPoints.isNotEmpty()) {
            //if is here and has already geoPoints, check

            val trip = Polyline(map)
            for (point in (activity as MainActivity?)!!.geoPoints)
                trip.addPoint(point)
            map.overlays.add(trip)
            mapController.setCenter((activity as MainActivity?)!!.geoPoints.elementAt((activity as MainActivity?)!!.geoPoints.size / 2))
        }else{
            //TODO map overlay
            //map.overlays.
        }


        binding.buttonEdit.setOnClickListener {
            if ((activity as MainActivity?)!!.currentTrip != null) {
                //put
                binding.buttonEdit.text = "update"
            } else {
                //post
                binding.buttonEdit.text = "save"
                if ((activity as MainActivity?)!!.currentTour != null) {
                    val coordinates = mutableListOf<Coordinates>()
                    for (geopoint in (activity as MainActivity?)!!.geoPoints)
                        coordinates.add(
                            Coordinates(
                                geopoint.longitude,
                                geopoint.latitude,
                                geopoint.altitude
                            )
                        )

                    val apiService = ApiService()
                    val newTrip = Trip(
                        title = binding.textViewTitle.text.toString(),
                        text = binding.textViewText.text.toString(),
                        coordinates = coordinates
                    )
                    //TODO change
                    val tourId = (activity as MainActivity?)!!.currentTour?.id!!
                    apiService.addTrip(tourId,newTrip) { trip ->
                        var barText = "Failed to create Trip."
                        if (trip != null) {
                            Log.i("upload", trip.id.toString())
                            barText = "Added Trip ${trip.title} with Id ${trip.id}"
                        }
                        Snackbar.make(view, barText, Snackbar.LENGTH_LONG)
                            .setAction("action", null).show()
                    }
                    //findNavController().navigate(R.id.action_TripFragment_to_TripsFragment)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}