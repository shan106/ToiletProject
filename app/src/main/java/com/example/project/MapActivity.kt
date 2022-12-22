package com.example.project

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import kotlin.collections.ArrayList


class MapActivity : AppCompatActivity() , OnMapReadyCallback, GoogleMap.OnMarkerClickListener,OnMapLongClickListener {
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mMap: GoogleMap
    private lateinit var allPoints: ArrayList<Plaatsen>
    private val REQUESTLOCATION = 99
    lateinit var db : DBHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var  currentLocation : Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map2)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        db = DBHelper((this))
        allPoints = db.getData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }

        val apiKey = "AIzaSyD2dlwp3hTYX6H4AvorYgeMEJ1ebW2pXC8"
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try{
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    currentLocation = location
                }
        }catch (e : Exception){e.printStackTrace()}
    }

    private fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUESTLOCATION)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUESTLOCATION)

            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUESTLOCATION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] === PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.isMyLocationEnabled = true
                        try{
                            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location : Location? ->
                                    currentLocation = location
                                }
                        }catch (e : Exception){e.printStackTrace()}
                    }
                } else {
                    Toast.makeText(
                        this, "permission denied",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL;
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient();
                mMap.isMyLocationEnabled = true;
            }
        } else {
            buildGoogleApiClient();
            mMap.isMyLocationEnabled = true;
        }
        var lat:Double = 43.651070
        var long:Double = -79.347015

        mMap.clear()
        val sydney = LatLng(lat, long)
        mMap.addMarker(
            MarkerOptions().position(sydney)
                .title("Marker in temp")
        )
        val cameraPosition = CameraPosition.Builder().target(sydney).zoom(14.0f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mMap.setOnMarkerClickListener (this)
        mMap.setOnMapLongClickListener(this)
        setUpLocations()

    }

    private fun setUpLocations() {
        for(i in 0 until allPoints.size){
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(allPoints[i].lat,allPoints[i].long))
                    .title(allPoints[i].adres)
                    .zIndex(i.toFloat())
            )
        }
        try{
            val cameraPosition = CameraPosition.Builder().target(LatLng(allPoints[0].lat,allPoints[0].long)).zoom(14.0f).build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val index = marker.zIndex.toInt()
        val listItem = allPoints[index]
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.toilet_row_new)
        bottomSheetDialog.findViewById<TextView>(R.id.tvadres)!!.text = listItem.adres
        bottomSheetDialog.findViewById<TextView>(R.id.tvgeslacht)!!.text = listItem.geslacht
        bottomSheetDialog.findViewById<TextView>(R.id.tvrolstoel)!!.text = "Rolstoel "+listItem.rolstoel
        bottomSheetDialog.findViewById<TextView>(R.id.tvluiertafel)!!.text = "Luiertafel "+listItem.luiertafel
        if(currentLocation == null)
            bottomSheetDialog.findViewById<TextView>(R.id.tv_current_distance)!!.text  = "Current Location Not found!"
        else {
            val itemLocation = Location("custom_location")
            itemLocation.latitude = listItem.lat
            itemLocation.longitude = listItem.long
            val distance = currentLocation!!.distanceTo(itemLocation).toString()
            bottomSheetDialog.findViewById<TextView>(R.id.tv_current_distance)!!.text = distance + " m"
        }

        bottomSheetDialog.findViewById<CardView>(R.id.remove)!!.visibility = View.VISIBLE
        bottomSheetDialog.findViewById<CardView>(R.id.remove)!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            db.delete(listItem.id.toString())
            allPoints.remove(listItem)
            mMap.clear()
            setUpLocations()
        }
        bottomSheetDialog.show()
        return false
    }
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient.connect()
    }

    override fun onMapLongClick(latlng: LatLng) {
        val plaatsen = Plaatsen()
        val geocoder: Geocoder
        val addresses: List<Address>?
        geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latlng.latitude,
            latlng.longitude,
            1
        )
        try {
            val address: String =
                addresses!![0].getAddressLine(0)
            plaatsen.adres = address
        }catch (e : java.lang.Exception){
            e.printStackTrace()
            plaatsen.adres = "No address"
        }

        plaatsen.lat =latlng.latitude
        plaatsen.long =latlng.longitude

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.toilet_row_new_data_add)
        bottomSheetDialog.findViewById<TextView>(R.id.tvadres)!!.text = plaatsen.adres
        bottomSheetDialog.findViewById<CardView>(R.id.save)!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            plaatsen.rolstoel = bottomSheetDialog.findViewById<SwitchCompat>(R.id.rolstoel_switch)!!.isChecked
            plaatsen.luiertafel = bottomSheetDialog.findViewById<SwitchCompat>(R.id.rolstoel_switch)!!.isChecked
            Log.e("***boolen","rolstoel "+ plaatsen.rolstoel)
            Log.e("***boolen","luiertafel "+ plaatsen.luiertafel)
            db.insertData(plaatsen)
            allPoints.add(plaatsen)
            mMap.clear()
            setUpLocations()
        }
        bottomSheetDialog.show()
    }
}