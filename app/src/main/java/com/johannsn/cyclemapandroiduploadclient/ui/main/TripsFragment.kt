package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.johannsn.cyclemapandroiduploadclient.R
import com.johannsn.cyclemapandroiduploadclient.databinding.FragmentTripsBinding
import com.johannsn.cyclemapandroiduploadclient.service.ApiService
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val tripsList = mutableListOf<Trip>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar!!.setSubtitle(R.string.select_trip)

        (activity as MainActivity).currentTrip = null
        val swipeRefreshLayout = binding.refreshLayout
        swipeRefreshLayout.setOnRefreshListener {

            loadTrips()
            swipeRefreshLayout.isRefreshing = false
        }
        //TODO do not always reload
        loadTrips()
    }

    //TODO use template?
    private fun loadTrips() {
        val listView: ListView = binding.listView
        val adapter = ArrayAdapter(requireActivity(), R.layout.listview_item, tripsList)
        val tour = (activity as MainActivity?)!!.currentTour
        if(tour != null){
            val apiService = ApiService()
            apiService.getTripsForTour(tour.id!!){ trips ->
                if (trips != null) {
                    binding.fab.visibility = View.VISIBLE
                    binding.listView.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE
                    binding.fab.setOnClickListener {
                        findNavController().navigate(R.id.action_TripsFragment_to_TripFragment)
                    }
                    if(trips.size > 0) {
                        tripsList.clear()
                        tripsList.addAll(trips)
                        listView.adapter = adapter
                        //TODO move to tours, beginning and end
                        listView.onItemClickListener =
                            AdapterView.OnItemClickListener { _, _, position, _ ->
                                binding.myProgress.visibility = View.VISIBLE
                                binding.textViewLoading.visibility = View.VISIBLE
                                binding.myProgress.progress = 40

                                val clickedTrip = listView.getItemAtPosition(position) as Trip
                                (activity as MainActivity).currentTrip = clickedTrip
                                if((activity as MainActivity).currentCoordinates.isEmpty())
                                    (activity as MainActivity).currentCoordinates = clickedTrip.coordinates!!
                                else{
                                    //Trip Coordinates have been shared by other app and received
                                    //TODO show dialog

                                }
                                findNavController().navigate(R.id.action_TripsFragment_to_TripFragment)
                            }
                    } else {
                        binding.textViewError.visibility = View.VISIBLE
                        binding.textViewError.setText(R.string.no_trips_for_tour)
                    }
                }
                else
                {
                    binding.textViewError.visibility = View.VISIBLE
                    binding.fab.visibility = View.GONE
                    binding.listView.visibility = View.GONE
                    tripsList.clear()
                    adapter.notifyDataSetChanged()
                    Snackbar.make(requireView(),R.string.failed_to_load, Snackbar.LENGTH_LONG)
                        .show()
                    //TODO catch error
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}