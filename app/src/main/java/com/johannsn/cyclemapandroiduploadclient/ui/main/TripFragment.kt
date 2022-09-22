package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.johannsn.cyclemapandroiduploadclient.R
import com.johannsn.cyclemapandroiduploadclient.databinding.FragmentTripBinding
import com.johannsn.cyclemapandroiduploadclient.service.ApiService
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
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
        if ((activity as MainActivity).currentCoordinates.isNotEmpty()) {
            //if is here and has already geoPoints, check

            val trip = Polyline(map)
            for (coordinate in (activity as MainActivity).currentCoordinates)
                if(coordinate.lat != null && coordinate.lng != null)
                    trip.addPoint(GeoPoint(coordinate.lat,coordinate.lng))

            map.overlays.add(trip)
            //TODO center map
            //mapController.activity as MainActivity?)!!.geoPoints.elementAt((activity as MainActivity?)!!.geoPoints.size / 2))
        }else{
            //TODO map overlay
            //map.overlays.
        }


        binding.buttonEdit.setOnClickListener {
            if ((activity as MainActivity).currentTrip != null) {
                //put
                (activity as MainActivity).supportActionBar!!.setSubtitle(R.string.update_trip)
                binding.buttonEdit.text = R.string.update.toString()
            } else {
                //post
                (activity as MainActivity).supportActionBar!!.setSubtitle(R.string.create_trip)
                binding.buttonEdit.text = R.string.save.toString()
                if ((activity as MainActivity).currentTour != null) {

                    val apiService = ApiService()
                    val newTrip = Trip(
                        title = binding.textViewTitle.text.toString(),
                        text = binding.textViewText.text.toString(),
                        coordinates = (activity as MainActivity).currentCoordinates
                    )
                    //TODO change
                    val tourId = (activity as MainActivity?)!!.currentTour?.id!!
                    apiService.addTrip(tourId,newTrip) { trip ->
                        var barText = R.string.failed_to_create_trip.toString()
                        if (trip != null) {
                            barText = R.string.create_trip.toString() + "${trip.title}(${trip.id})."
                        }
                        Snackbar.make(view, barText, Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
            //Do in both cases
            (activity as MainActivity).currentCoordinates.clear()
            //findNavController().navigate(R.id.action_TripFragment_to_TripsFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}