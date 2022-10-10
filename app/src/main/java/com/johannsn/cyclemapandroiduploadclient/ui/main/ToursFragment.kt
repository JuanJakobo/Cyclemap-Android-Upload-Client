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
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ToursFragment : Fragment() {

    private var _binding: FragmentToursBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val toursList = mutableListOf<Tour>()
    private var loading = false
    private val apiService = ApiService()
    private var adapter: ArrayAdapter<Tour>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentToursBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setSubtitle(R.string.select_tour)
        binding.downloadProgressCycle.visibility = View.VISIBLE

        adapter = ArrayAdapter(requireActivity(),R.layout.listview_item, toursList)
        createListViewAdapterListener()

        //Add tour fab
        binding.fab.setOnClickListener { view ->
            val editDialogBuilder = AlertDialog.Builder(activity)
            editDialogBuilder.setTitle(R.string.add_new_tour)
            val editTour = EditText(activity)
            editDialogBuilder.setView(editTour)
            editDialogBuilder.setPositiveButton(
                R.string.add_tour
            ) { _, _ ->
                val newTour = Tour(title = editTour.text.toString())
                binding.downloadProgressCycle.visibility = View.VISIBLE
                apiService.addTour(newTour) { tour ->
                    var barText = getString(R.string.failed_to_create_tour)
                    if (tour != null) {
                        toursList.add(tour)
                        adapter?.notifyDataSetChanged()
                        binding.listView.visibility = View.VISIBLE
                        binding.textViewError.visibility = View.GONE
                        barText = getString(R.string.added_tour,tour.title,tour.id)
                    }
                    binding.downloadProgressCycle.visibility = View.GONE
                    Snackbar.make(view, barText, Snackbar.LENGTH_LONG)
                        .show()
                }
            }
            editDialogBuilder.setNegativeButton(R.string.cancel) { _, _ ->
            }
            val dialog = editDialogBuilder.create()
            dialog.show()
        }

        (activity as MainActivity).currentTour = null

        val swipeRefreshLayout = binding.refreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            if(!loading) {
                loadTours()
            }
            swipeRefreshLayout.isRefreshing = false
        }
        //TODO does not load if passed items
        loadTours()

    }

    private fun createListViewAdapterListener() {
        val listView: ListView = binding.listView
        listView.adapter = adapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val clickedTour = listView.getItemAtPosition(position) as Tour
                (activity as MainActivity).currentTour = clickedTour
                findNavController().navigate(R.id.action_ToursFragment_to_TripsFragment)
            }
        listView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { view, _, position, _ ->
                val clickedTour = listView.getItemAtPosition(position) as Tour
                val dialogBuilder = AlertDialog.Builder(activity)

                dialogBuilder.setTitle(getString(R.string.edit_tour,clickedTour.id))
                val editTour = EditText(activity)
                editTour.setText(clickedTour.title)
                dialogBuilder.setView(editTour)
                dialogBuilder.setPositiveButton(
                    R.string.update
                ) { _, _ ->
                    /*
                    //TODO implement
                    val tour = Tour(title = editTour.text.toString())
                    binding.downloadProgressCycle.visibility = View.VISIBLE
                    apiService.updateTour(tour) { tour ->
                        var barText = "Failed to update Tour"
                        if (tour != null) {
                            clickedTour = tour
                            adapter.notifyDataSetChanged()
                            barText = "Updated Tour"
                        }
                        binding.downloadProgressCycle.visibility = View.GONE
                     */
                    Snackbar.make(view, R.string.not_implemented, Snackbar.LENGTH_LONG)
                        .show()

                }
                dialogBuilder.setNegativeButton(
                    R.string.delete
                ) { _, _ ->
                    binding.downloadProgressCycle.visibility = View.VISIBLE
                    apiService.deleteTour(clickedTour.id){
                        var barText = getString(R.string.failed_to_delete_tour)
                        if (it) {
                            toursList.remove(clickedTour)
                            adapter?.notifyDataSetChanged()
                            barText = getString(R.string.deleted_tour, clickedTour.title, clickedTour.id)
                            if(toursList.isEmpty()) {
                                binding.textViewError.visibility = View.VISIBLE
                                binding.listView.visibility = View.GONE
                                binding.textViewError.setText(R.string.no_tours)
                            }
                        }
                        binding.downloadProgressCycle.visibility = View.GONE
                        Snackbar.make(view, barText, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    binding.downloadProgressCycle.visibility = View.GONE
                }
                dialogBuilder.setNeutralButton(R.string.cancel) { _, _ ->
                }
                val dialog = dialogBuilder.create()
                dialog.show()
                true
            }
    }

    private fun loadTours(){
        loading = true
        apiService.getTours { tours ->
            if (tours != null) {
                binding.fab.visibility = View.VISIBLE
                binding.listView.visibility = View.VISIBLE
                binding.textViewError.visibility = View.GONE
                if(tours.size > 0) {
                    toursList.clear()
                    toursList.addAll(tours)
                    adapter?.notifyDataSetChanged()
                    binding.listView.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE
                }else{
                    binding.textViewError.visibility = View.VISIBLE
                    binding.listView.visibility = View.GONE
                    binding.textViewError.setText(R.string.no_tours)
                }

            } else {
                binding.textViewError.visibility = View.VISIBLE
                binding.fab.visibility = View.GONE
                binding.listView.visibility = View.GONE
                toursList.clear()
                adapter?.notifyDataSetChanged()
                Snackbar.make(requireView(),R.string.failed_to_load, Snackbar.LENGTH_LONG)
                    .show()
            }
            binding.downloadProgressCycle.visibility = View.GONE
        }
        loading = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}