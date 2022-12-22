package com.example.project

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.OverlayItem
import java.io.File
import java.net.URL

class Map : AppCompatActivity() {
    private lateinit var mMapView: MapView
    private var mMyLocationOverlay: ItemizedOverlay<OverlayItem>? = null
    private var items = ArrayList<OverlayItem>()
    private var clearButton: Button? = null
    private val urlNominatim = "https://nominatim.openstreetmap.org/"
    private var notificationManager: NotificationManager? = null
    private var mChannel: NotificationChannel? = null
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val osmConfig = Configuration.getInstance()
        osmConfig.userAgentValue = packageName
        val basePath = File(cacheDir.absolutePath, "osmdroid")
        osmConfig.osmdroidBasePath = basePath
        val tileCache = File(osmConfig.osmdroidBasePath, "tile")
        osmConfig.osmdroidTileCache = tileCache

        setContentView(R.layout.activity_map)
        mMapView = findViewById(R.id.mapview)

        clearButton = findViewById(R.id.clear_button)
        clearButton?.setOnClickListener {
            mMapView?.overlays?.clear()
            // Redraw map
            mMapView?.invalidate()
        }
        if (hasPermissions()) {
            initMap()
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), 100)
        }

    }
    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            if (hasPermissions()){
                initMap()
            }else{
                finish()
            }
        }
    }

    private fun initMap() {
        mMapView?.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                //Toast.makeText(baseContext, p.latitude.toString() + " - " + p.longitude, Toast.LENGTH_LONG).show()
                val url = URL(urlNominatim + "reverse?lat=" + p.latitude.toString() + "&lon=" + p.longitude.toString() + "&format=json")
                //val task = MyAsyncTask()
                //task.execute(url)

                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        mMapView?.overlays?.add(MapEventsOverlay(mReceive))
        mMapView?.controller?.setZoom(17.0)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val userlatitude = userlocation?.latitude
        val userlongitude = userlocation?.longitude
        setCenter(GeoPoint(userlatitude!!,userlongitude!!), "YOU")


    }

    private fun addMarker(geoPoint: GeoPoint, name: String) {
        items.add(OverlayItem(name, name, geoPoint))
        mMyLocationOverlay = ItemizedIconOverlay(items, null, applicationContext)
        mMapView?.overlays?.add(mMyLocationOverlay)
    }

    private fun setCenter(geoPoint: GeoPoint, name: String) {
        mMapView?.controller?.setCenter(geoPoint)
        addMarker(geoPoint, name)
    }
}