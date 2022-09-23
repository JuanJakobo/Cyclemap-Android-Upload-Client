package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.johannsn.cyclemapandroiduploadclient.R
import com.johannsn.cyclemapandroiduploadclient.databinding.FragmentToursBinding
import com.johannsn.cyclemapandroiduploadclient.service.ApiService
import com.johannsn.cyclemapandroiduploadclient.service.models.Tour


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ToursFragment : Fragment() {

    private var _binding: FragmentToursBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val toursList = mutableListOf<Tour>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentToursBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO move strings to string file
        (activity as MainActivity).supportActionBar?.setSubtitle(R.string.select_tour)

        (activity as MainActivity).currentTour = null
        val swipeRefreshLayout = binding.refreshLayout
        swipeRefreshLayout.setOnRefreshListener {

            loadTours()
            swipeRefreshLayout.isRefreshing = false
        }
        //TODO add loading screen
        //TODO if tours are loaded, do not reload, therefore should not delete them in trips
        //ah tours not saved anywhere, save?
        loadTours()

    }

    private fun loadTours(){
        val listView: ListView = binding.listView
        val adapter  = ArrayAdapter(requireActivity(),R.layout.listview_item,toursList)
        val apiService = ApiService()
        apiService.getTours { tours ->
            if (tours != null) {
                binding.fab.visibility = View.VISIBLE
                binding.listView.visibility = View.VISIBLE
                binding.textViewError.visibility = View.GONE
                binding.fab.setOnClickListener { view ->
                    val editDialogBuilder = AlertDialog.Builder(activity)
                    editDialogBuilder.setTitle(R.string.add_new_tour)
                    val editTour = EditText(activity)
                    editDialogBuilder.setView(editTour)
                    editDialogBuilder.setPositiveButton(
                        R.string.add_tour
                    ) { _, _ ->
                        val newTour = Tour(title = editTour.text.toString())
                        apiService.addTour(newTour) { tour ->
                            var barText = R.string.failed_to_create_tour.toString()
                            if (tour != null) {
                                toursList.add(tour)
                                adapter.notifyDataSetChanged()
                                barText =
                                    R.string.added_tour.toString() + " ${tour.title}(${tour.id})"
                            }
                            Snackbar.make(view, barText, Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }

                    editDialogBuilder.setNegativeButton(R.string.cancel) { _, _ ->
                    }
                    val dialog = editDialogBuilder.create()
                    dialog.show()
                }
                if(tours.size > 0) {
                    //TODO return also trips?
                    toursList.clear()
                    toursList.addAll(tours)
                    adapter.notifyDataSetChanged()
                    listView.adapter = adapter
                    listView.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, position, _ ->
                            val clickedTour = listView.getItemAtPosition(position) as Tour
                            (activity as MainActivity).currentTour = clickedTour
                            findNavController().navigate(R.id.action_ToursFragment_to_TripsFragment)
                        }
                }
            } else {
                binding.textViewError.visibility = View.VISIBLE
                binding.fab.visibility = View.GONE
                binding.listView.visibility = View.GONE
                toursList.clear()
                adapter.notifyDataSetChanged()
                Snackbar.make(requireView(),R.string.failed_to_load, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}