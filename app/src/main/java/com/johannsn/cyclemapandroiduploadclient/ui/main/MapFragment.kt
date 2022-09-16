package com.johannsn.cyclemapandroiduploadclient.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.johannsn.cyclemapandroiduploadclient.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import java.util.*

var geoPoints = Vector<GeoPoint>()

class MainFragment : Fragment() {
    private lateinit var map : MapView

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map = view.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        val trip = Polyline(map)
        if(!geoPoints.isEmpty()) {
            for (point in geoPoints)
                trip.addPoint(point)
            map.overlays.add(trip)
            mapController.setCenter(geoPoints.elementAt(geoPoints.size / 2))
        }
        mapController.setZoom(11.1)

        val gson = Gson()
        val geoPointJson: String = gson.toJson(geoPoints)
        Log.i("Json",geoPointJson)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        // TODO: Use the ViewModel
    }

}