package com.johannsn.cyclemapandroiduploadclient.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.johannsn.cyclemapandroiduploadclient.R
import com.johannsn.cyclemapandroiduploadclient.databinding.ActivityMainBinding
import com.johannsn.cyclemapandroiduploadclient.service.FitToGeo
import com.johannsn.cyclemapandroiduploadclient.service.models.Tour
import com.johannsn.cyclemapandroiduploadclient.service.models.Trip
import org.osmdroid.config.Configuration
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    //TODO pass as bundle?
    internal var sharedTrip : Trip? = null
    internal var gotSharedCoordinates = false

    internal var currentTour : Tour? = null
    internal var currentTrip : Trip? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(intent  != null) {
            if (intent.type.equals("message/rfc822")) {
                val stream = if(intent.action === Intent.ACTION_SEND_MULTIPLE) intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM) else null
                if (stream != null) {
                    val uri = Uri.parse(stream[stream.size-1].toString())
                    try {
                        val test = FitToGeo()
                        val contentResolver = contentResolver
                        val inputStream = contentResolver?.openInputStream(uri)
                        if(inputStream != null) {
                            sharedTrip = test.readInFit(inputStream)
                            gotSharedCoordinates = true
                        }
                    } catch (e: IOException) {
                        throw RuntimeException("Error opening file")
                    }
                }
            }
        }

        //TODO add login fragment
        val sp: SharedPreferences = this.getSharedPreferences("loginSaved", MODE_PRIVATE)
        val url = sp.getString("url",null)
        val username = sp.getString("username", null)
        val password = sp.getString("password", null)
        //if (username != null && password != null && url != null) {
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            appBarConfiguration = AppBarConfiguration(navController.graph)
            Log.i("tester", "$url and $username and $password")
        //} else {
        //}

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //TODO handle options menu
        Log.i("tester","options called")
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    //https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Kotlin)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }
}