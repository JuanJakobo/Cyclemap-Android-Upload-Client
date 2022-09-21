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
//TODO put together with tour
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

        val swipeRefreshLayout = binding.refreshLayout
        swipeRefreshLayout.setOnRefreshListener {

            loadTrips()
            swipeRefreshLayout.isRefreshing = false
        }
        loadTrips()
    }

    fun loadTrips() {
        val listView: ListView = binding.listView // view.findViewById(R.id.listView)
        val adapter = ArrayAdapter(requireActivity(), R.layout.listview_item, tripsList)
        //TODO
        val tourId = arguments?.getLong("TourId")
        if(tourId != null){
            val apiService = ApiService()
            apiService.getTripsForTour(tourId){
                if (it != null) {
                    binding.fab.visibility = View.VISIBLE
                    binding.listView.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE
                    binding.fab.setOnClickListener { _ ->
                        val bundle = Bundle()
                        bundle.putLong("TourId", tourId)
                        findNavController().navigate(R.id.action_TripsFragment_to_TripFragment,bundle)
                    }
                    if(it.size > 0) {
                        tripsList.clear()
                        tripsList.addAll(it)
                        listView.adapter = adapter
                        listView.onItemClickListener =
                            AdapterView.OnItemClickListener { _, _, position, id -> // value of item that is clicked
                                binding.myProgress.visibility = View.VISIBLE
                                binding.textViewLoading.visibility = View.VISIBLE
                                binding.myProgress.progress = 40

                                val clickedTrip = listView.getItemAtPosition(position) as Trip
                                //TODO here also Tour ID and put together
                                val bundle = Bundle()
                                if(clickedTrip.id != null)
                                    bundle.putLong("TripId", clickedTrip.id)

                                findNavController().navigate(R.id.action_TripsFragment_to_TripFragment,bundle)
                            }
                    } else {
                        binding.textViewError.visibility = View.VISIBLE
                        binding.textViewError.text = "There are no trips for this tour."
                    }
                }
                else
                {
                    binding.textViewError.visibility = View.VISIBLE
                    binding.fab.visibility = View.GONE
                    binding.listView.visibility = View.GONE
                    tripsList.clear()
                    adapter.notifyDataSetChanged()
                    val barText = "Failed to load."
                    Snackbar.make(requireView(),barText, Snackbar.LENGTH_LONG)
                        .setAction("action",null).show()
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