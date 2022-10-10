package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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


        val currentTrip = (activity as MainActivity).currentTrip
        if (currentTrip != null) {
            binding.textViewData.text = getString(R.string.tourdata,currentTrip.distance,currentTrip.ascent,currentTrip.descent)
            if (currentTrip.coordinates.isNotEmpty()) {
                val map = binding.map
                map.visibility = View.VISIBLE
                map.setTileSource(TileSourceFactory.MAPNIK)
                map.setMultiTouchControls(true)
                val line = Polyline(map)
                for (coordinate in currentTrip.coordinates)
                    line.addPoint(GeoPoint(coordinate.lat, coordinate.lng))
                map.overlays.add(line)
                map.post { map.zoomToBoundingBox(line.bounds.increaseByScale(1.7f), false) }
            } else {
                binding.map.visibility = View.GONE
            }

            val update = currentTrip.text.isNotEmpty() && currentTrip.title.isNotEmpty()

            if (update) {
                //put
                (activity as MainActivity).supportActionBar!!.setSubtitle(R.string.update_trip)
                binding.buttonEdit.setText(R.string.update)
                binding.textViewTitle.setText((activity as MainActivity).currentTrip?.title)
                binding.textViewText.setText((activity as MainActivity).currentTrip?.text)
            } else {
                //post
                (activity as MainActivity).supportActionBar!!.setSubtitle(R.string.create_trip)
                binding.buttonEdit.setText(R.string.save)
            }
            binding.buttonEdit.setOnClickListener {
                if (binding.textViewTitle.text.isBlank() || binding.textViewText.text.isBlank()) {
                    Snackbar.make(view, "Title and Text cannot be empty.", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    val apiService = ApiService()
                    binding.downloadProgressCycle.visibility = View.VISIBLE
                    //val coordinates = (activity as MainActivity).currentCoordinates
                    val newTrip = Trip(
                        title = binding.textViewTitle.text.toString(),
                        text = binding.textViewText.text.toString(),
                        coordinates = currentTrip.coordinates,
                        distance = currentTrip.distance,
                        ascent = currentTrip.ascent,
                        descent = currentTrip.descent,
                    )
                    if (update) {
                        newTrip.tour = currentTrip.tour


                        drawResult(view, getString(R.string.not_implemented), false)
                        /*
                        TODO implement
                        apiService.updateTrip(newTrip) { trip ->
                            var sucess = false
                            var barText = "Failed to update"
                            if (trip != null) {
                                barText =
                                    R.string.create_trip.toString() + "Updated ${trip.title}. (${trip.id})."
                                sucess = true
                            }
                            drawResult(view, barText, sucess)
                        }

                    } else {
                        (activity as MainActivity).currentTour?.id?.let {
                            apiService.addTrip(it, newTrip) { trip ->
                                var sucess = false
                                var barText = "Failed to create Trip"
                                if (trip != null) {
                                    barText =
                                        R.string.create_trip.toString() + "Created ${trip.title}. (${trip.id})."
                                    sucess = true
                                }
                                drawResult(view, barText, sucess)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun drawResult(currentView: View, barText: String, sucess: Boolean){
        binding.downloadProgressCycle.visibility = View.GONE
        Snackbar.make(currentView, barText, Snackbar.LENGTH_LONG)
            .show()
        if (sucess) {
            (activity as MainActivity).gotSharedCoordinates = false
            findNavController().navigate(R.id.action_TripFragment_to_TripsFragment)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}