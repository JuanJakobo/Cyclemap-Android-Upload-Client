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
import com.google.android.material.snackbar.Snackbar
import com.johannsn.cyclemapandroiduploadclient.R
import com.johannsn.cyclemapandroiduploadclient.databinding.FragmentTourBinding
import com.johannsn.cyclemapandroiduploadclient.service.ApiService
import com.johannsn.cyclemapandroiduploadclient.service.models.Tour

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TourFragment : Fragment() {

    private var _binding: FragmentTourBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTourBinding.inflate(inflater, container, false)
        return binding.root

    }

    //TODO move where?
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = ApiService()
        apiService.getTours { tours ->
            if (tours != null) {
                val listView: ListView = binding.listView // view.findViewById(R.id.listView)
                val adapter  = ArrayAdapter(requireActivity(),R.layout.listview_item,tours)
                listView.adapter = adapter
                binding.fab.setOnClickListener { view ->
                    val tour = Tour( title = "Test")
                    apiService.addTour(tour) { tour ->
                        var barText = "Failed to create user."
                        if (tour != null) {
                            Log.i("json", tour.id.toString())
                            tours.add(tour)
                            adapter.notifyDataSetChanged()
                            barText = "Added Tour ${tour.id}"
                        }
                        Snackbar.make(view,barText, Snackbar.LENGTH_LONG)
                            .setAction("action",null).show()
                    }
                }
                listView.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ -> // value of item that is clicked

                        val clickedTour = listView.getItemAtPosition(position) as Tour
                        val bundle = Bundle()
                        bundle.putLong("TourId", clickedTour.id!!)
                        findNavController().navigate(R.id.action_TourFragment_to_TripFragment,bundle)
                    }
            }
            else {
                //TODO catch error
            }
        }

    }
    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        // TODO: Use the ViewModel
    }
     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}