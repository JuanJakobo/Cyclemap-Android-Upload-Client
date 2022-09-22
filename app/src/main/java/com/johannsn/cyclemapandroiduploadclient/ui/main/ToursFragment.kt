package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.app.AlertDialog
import android.content.DialogInterface
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

    //TODO move where?
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity?)!!.currentTour = null
        val swipeRefreshLayout = binding.refreshLayout
        swipeRefreshLayout.setOnRefreshListener {

            loadTours()
            swipeRefreshLayout.isRefreshing = false
        }
        //TODO add loading screen
        loadTours()

    }

    fun loadTours(){
        val listView: ListView = binding.listView
        val adapter  = ArrayAdapter(requireActivity(),R.layout.listview_item,toursList)
        val apiService = ApiService()
        apiService.getTours { tours ->
            if (tours != null) {
                //TODO test if id > 0
                //return also tours?
                toursList.clear()
                toursList.addAll(tours)
                adapter.notifyDataSetChanged()
                listView.adapter = adapter
                binding.fab.visibility = View.VISIBLE
                binding.listView.visibility = View.VISIBLE
                binding.textViewError.visibility = View.GONE
                listView.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val clickedTour = listView.getItemAtPosition(position) as Tour
                        (activity as MainActivity?)!!.currentTour = clickedTour
                        findNavController().navigate(R.id.action_ToursFragment_to_TripsFragment)
                        //val bundle = Bundle()
                        //if(clickedTour.id != null)
                            //bundle.putLong("TourId", clickedTour.id)
                        //findNavController().navigate(R.id.action_ToursFragment_to_TripsFragment,bundle)
                    }
                binding.fab.setOnClickListener { view ->
                    val editDialogBuilder = AlertDialog.Builder(activity)
                    editDialogBuilder.setTitle("Add new Tour")
                    val editTour = EditText(activity)
                    editDialogBuilder.setView(editTour)
                    editDialogBuilder.setPositiveButton("Add Tour",
                        DialogInterface.OnClickListener { _, _ ->
                            val tour = Tour( title = editTour.text.toString())
                            apiService.addTour(tour) { tour ->
                                var barText = "Failed to create Tour."
                                if (tour != null) {
                                    toursList.add(tour)
                                    adapter.notifyDataSetChanged()
                                    barText = "Added Tour ${tour.title} with Id ${tour.id}"
                                }
                                Snackbar.make(view,barText, Snackbar.LENGTH_LONG)
                                    .setAction("action",null).show()
                            }
                        })

                    editDialogBuilder.setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { _, _ ->
                        })

                    val dialog = editDialogBuilder.create()
                    dialog.show()

                }
            }
            else {
                binding.textViewError.visibility = View.VISIBLE
                binding.fab.visibility = View.GONE
                binding.listView.visibility = View.GONE
                toursList.clear()
                adapter.notifyDataSetChanged()
                val barText = "Failed to load."
                Snackbar.make(requireView(),barText, Snackbar.LENGTH_LONG)
                    .setAction("action",null).show()
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