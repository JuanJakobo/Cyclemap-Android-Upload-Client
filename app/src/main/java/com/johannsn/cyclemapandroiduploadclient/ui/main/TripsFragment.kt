package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.app.AlertDialog
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
        binding.downloadProgressCycle.visibility = View.VISIBLE

        (activity as MainActivity).currentTrip = null
        val swipeRefreshLayout = binding.refreshLayout
        swipeRefreshLayout.setOnRefreshListener {

            loadTrips()
            swipeRefreshLayout.isRefreshing = false
        }
        //TODO do not always reload
        loadTrips()
    }


    private fun createListViewAdapterListener(){
        val listView: ListView = binding.listView
        listView.adapter = adapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->

                binding.downloadProgressCycle.visibility = View.VISIBLE

                val clickedTrip = listView.getItemAtPosition(position) as Trip
                (activity as MainActivity).currentTrip = clickedTrip

                if ((activity as MainActivity).gotSharedCoordinates) {
                    if(clickedTrip.coordinates.isNotEmpty()) {
                        val dialogBuilder = AlertDialog.Builder(activity)
                        dialogBuilder.setTitle(R.string.trip_already_got_coordinates)
                        dialogBuilder.setPositiveButton(
                            R.string.add_before
                        ) { _, _ ->
                            (activity as MainActivity).sharedTrip?.let {
                                clickedTrip.addAtBegin(
                                    it
                                )
                            }

                            findNavController().navigate(R.id.action_TripsFragment_to_TripFragment)
                        }
                        dialogBuilder.setNegativeButton(
                            R.string.add_after
                        ) { _, _ ->
                            (activity as MainActivity).sharedTrip?.let {
                                clickedTrip.addAtEnd(
                                    it
                                )
                            }

                            findNavController().navigate(R.id.action_TripsFragment_to_TripFragment)
                        }

                        dialogBuilder.setNeutralButton(R.string.cancel) { _, _ ->
                        }
                        val dialog = dialogBuilder.create()
                        dialog.show()
                    }
                    (activity as MainActivity).currentTrip = clickedTrip
                } else {
                    findNavController().navigate(R.id.action_TripsFragment_to_TripFragment)
                }
            }
        listView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { view, _, position, _ ->
                val clickedTrip = listView.getItemAtPosition(position) as Trip
                val dialogBuilder = AlertDialog.Builder(activity)
                dialogBuilder.setTitle(getString(R.string.do_you_want_to_delete_tour,clickedTrip.title))
                dialogBuilder.setPositiveButton(
                    R.string.delete_trip
                ) { _, _ ->
                    binding.downloadProgressCycle.visibility = View.VISIBLE
                    apiService.deleteTrip(clickedTrip){
                        var barText = getString(R.string.failed_to_delete_trip)
                        if (it) {
                            tripsList.remove(clickedTrip)
                            adapter?.notifyDataSetChanged()
                            barText = getString(R.string.deleted_trip,clickedTrip.title,clickedTrip.id)
                            if(tripsList.isEmpty()) {
                                binding.textViewError.visibility = View.VISIBLE
                                binding.listView.visibility = View.GONE
                                binding.textViewError.setText(R.string.no_tours)
                            }
                        }
                        Snackbar.make(view, barText, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    binding.downloadProgressCycle.visibility = View.GONE
                }

                dialogBuilder.setNegativeButton(R.string.cancel) { _, _ ->
                }
                val dialog = dialogBuilder.create()
                dialog.show()
                true
            }

    }

    //TODO use template?
    private fun loadTrips() {
        val listView: ListView = binding.listView
        val adapter = ArrayAdapter(requireActivity(),R.layout.listview_item, tripsList)
        val tour = (activity as MainActivity).currentTour
        if(tour != null){
            val apiService = ApiService()
            apiService.getTripsForTour(tour.id){ trips ->
                if (trips != null) {
                    binding.listView.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE

                    binding.fab.visibility = if((activity as MainActivity).gotSharedCoordinates) View.VISIBLE else View.GONE
                    binding.fab.setOnClickListener {
                        //TODO check if sharedTrip is null
                        (activity as MainActivity).currentTrip =  (activity as MainActivity).sharedTrip
                        if(!(activity as MainActivity).gotSharedCoordinates) {
                            (activity as MainActivity).sharedTrip?.coordinates?.clear()
                        }
                        findNavController().navigate(R.id.action_TripsFragment_to_TripFragment)
                    }
                    if(trips.size > 0) {
                        tripsList.clear()
                        tripsList.addAll(trips)
                        listView.adapter = adapter
                        listView.onItemClickListener =
                            AdapterView.OnItemClickListener { _, _, position, _ ->

                                binding.downloadProgressCycle.visibility = View.VISIBLE

                                val clickedTrip = listView.getItemAtPosition(position) as Trip
                                (activity as MainActivity).currentTrip = clickedTrip

                                if ((activity as MainActivity).gotSharedCoordinates) {
                                    if(clickedTrip.coordinates.isNotEmpty()) {
                                        val dialogBuilder = AlertDialog.Builder(activity)
                                        dialogBuilder.setTitle("The Trip already contains Coordinates. What do you want to do?")
                                        dialogBuilder.setPositiveButton(
                                            "Add before"
                                        ) { _, _ ->
                                            //get currentTrip once?
                                            //TODO has to add up later or rename...
                                            /*
                                            (activity as MainActivity).sharedTrip?.coordinates?.let {
                                                clickedTrip.coordinates.addAll(
                                                    it
                                                )
                                            }
                                            */
                                            //(activity as MainActivity).currentCoordinates = clickedTrip.coordinates
                                            findNavController().navigate(R.id.action_TripsFragment_to_TripFragment)
                                        }
                                        dialogBuilder.setNegativeButton(
                                            "Add after"
                                        ) { _, _ ->
                                            //TODO
                                            //((activity) as MainActivity).sharedTrip..addAll(
                                                //clickedTrip.coordinates
                                            //)
                                            findNavController().navigate(R.id.action_TripsFragment_to_TripFragment)
                                        }

                                        dialogBuilder.setNeutralButton(R.string.cancel) { _, _ ->
                                        }
                                        val dialog = dialogBuilder.create()
                                        dialog.show()
                                        //TODO use bundle
                                    }
                                    (activity as MainActivity).currentTrip =  (activity as MainActivity).sharedTrip
                                } else {
                                    /*
                                    if(clickedTrip.coordinates.isNotEmpty()) {
                                        //TODO pass via bundle?
                                        (activity as MainActivity).currentCoordinates =
                                            clickedTrip.coordinates
                                    }else {
                                        (activity as MainActivity).currentCoordinates.clear()
                                    }
                                     */
                                    findNavController().navigate(R.id.action_TripsFragment_to_TripFragment)
                                }
                            }
                        listView.onItemLongClickListener =
                            AdapterView.OnItemLongClickListener { _, _, position, _ ->
                                val clickedTrip = listView.getItemAtPosition(position) as Trip
                                val dialogBuilder = AlertDialog.Builder(activity)
                                dialogBuilder.setTitle("Are you sure you want to delete Trip $clickedTrip.")
                                dialogBuilder.setPositiveButton(
                                    "Delete Tour"
                                ) { _, _ ->
                                    binding.downloadProgressCycle.visibility = View.VISIBLE
                                    //TODO does not work
                                    apiService.deleteTrip(clickedTrip)
                                    //TODO only on success
                                    tripsList.remove(clickedTrip)
                                    adapter.notifyDataSetChanged()
                                    binding.downloadProgressCycle.visibility = View.GONE
                                }

                                dialogBuilder.setNegativeButton(R.string.cancel) { _, _ ->
                                }
                                val dialog = dialogBuilder.create()
                                dialog.show()
                                true
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
                binding.downloadProgressCycle.visibility = View.GONE

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}