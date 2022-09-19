package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import com.johannsn.cyclemapandroiduploadclient.R
import com.johannsn.cyclemapandroiduploadclient.databinding.FragmentTripBinding
import com.johannsn.cyclemapandroiduploadclient.service.ApiService
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
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

        val apiService = ApiService()
        apiService.getTripsForTour(arguments?.getLong("TourId",0)!!){
            if (it != null) {
                if(it.size > 0) {
                    val trips = mutableListOf<Trip>()
                    trips.addAll(it)
                    val listView: ListView = binding.listView // view.findViewById(R.id.listView)
                    val adapter = ArrayAdapter(requireActivity(), R.layout.listview_item, trips)
                    listView.adapter = adapter
                    listView.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, _, _ -> // value of item that is clicked
                            binding.myProgress.visibility = View.VISIBLE
                            binding.myProgress.progress = 40
                            binding.textView2.text = "loading"
                            findNavController().navigate(R.id.action_TripFragment_to_CoordinatesFragment)
                        }
                }
                else
                {
                    Log.i("json","no trips for this tour")
                    //TODO method to add some

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}