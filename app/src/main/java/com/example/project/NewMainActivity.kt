package com.example.project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.databinding.ActivityNewMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class NewMainActivity : AppCompatActivity() {
    lateinit var binding : ActivityNewMainBinding
    lateinit var db : DBHelper
    val list = ArrayList<Plaatsen>()
    var isOpen = false
    lateinit var adapter : ToiletAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUESTLOCATION = 99
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try{
            supportActionBar!!.hide()
        }catch (e : Exception){
            e.printStackTrace()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.button.setOnClickListener {
            startActivity(Intent(this@NewMainActivity,MapActivity::class.java))
        }
        db = DBHelper(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ToiletAdapter(this@NewMainActivity,list)
        binding.recyclerView.adapter = adapter

        val fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        val fabClosed = AnimationUtils.loadAnimation(this, R.anim.fab_closed)
        val fabRClock = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise)
        val fabanticlock = AnimationUtils.loadAnimation(this, R.anim.rotate_antclock)

        binding.filter.setOnClickListener {
            if (isOpen) {
                binding.handicap.startAnimation(fabClosed)
                binding.humanfilter.startAnimation(fabClosed)
                binding.baby.startAnimation(fabClosed)
                binding.humanfilter.startAnimation(fabRClock)
                isOpen = false
            } else {
                binding.handicap.startAnimation(fabOpen)
                binding.humanfilter.startAnimation(fabOpen)
                binding.baby.startAnimation(fabOpen)
                binding.filter.startAnimation(fabanticlock)
                binding.handicap.isClickable
                binding.humanfilter.isClickable
                binding.baby.isClickable
                isOpen = true
            }
        }
        binding.handicap.setOnClickListener {
            Log.e("***hand"," clicked ")
            val handicapList = ArrayList<Plaatsen>()
            for(i in 0 until list.size){
                Log.e("***hand"," item.rolstoel "+list[i].rolstoel)
                if(list[i].rolstoel) {
                    handicapList.add(list[i])
                }
            }
            adapter.setFilter(handicapList)
        }
        binding.baby.setOnClickListener {
            Log.e("***baby"," baby clicked ")
            val luiertafelList = ArrayList<Plaatsen>()
            for(i in 0 until list.size){
                Log.e("***baby"," item.luiertafel "+list[i].luiertafel)
                if(list[i].luiertafel) {
                    luiertafelList.add(list[i])
                }
            }
            adapter.setFilter(luiertafelList)
        }
        binding.humanfilter.setOnClickListener {
            adapter.setFilter(list)
        }

        binding.search.setOnClickListener {
            if(binding.searchView.visibility == View.VISIBLE){
                binding.title.visibility = View.VISIBLE
                binding.searchView.setText("")
                binding.searchView.visibility = View.GONE
                binding.search.setImageResource(R.drawable.ic_baseline_search_24)

            }else{
                binding.title.visibility = View.GONE
                binding.searchView.visibility = View.VISIBLE
                binding.search.setImageResource(R.drawable.ic_baseline_close_24)
            }
        }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count >= 1) {
                    val newList: ArrayList<Plaatsen> = ArrayList()
                    for (i in 0 until list.size) {
                        if (list[i].adres.toLowerCase().contains(s.toString().toLowerCase()) || list[i].geslacht.toLowerCase().contains(s.toString().toLowerCase()) ) {
                            newList.add(list[i])
                        }
                    }
                    adapter.setFilter(newList)
                } else {
                    adapter.setFilter(list)
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })

    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkLocationPermission()){
                getallData()
            }
        }
    }

    private fun getallData() {
        list.addAll(db.getData())
        Log.e("***Temp"," list size "+list.size)
        for(i  in 0 until list.size){
            Log.e("***Temp"," item.rolstoel "+" "+ i +" "+list[i].rolstoel)
        }
        for(item in list){
            Log.e("***Temp"," item.luiertafel "+item.luiertafel)
        }
        adapter.notifyDataSetChanged()
        try{
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    for(item in list){
                        try{
                            val itemLocation = Location("custom_location")
                            itemLocation.latitude = item.lat
                            itemLocation.longitude = item.long
                            item.distance = location!!.distanceTo(itemLocation).toString()
                        }catch (e : Exception){e.printStackTrace()}
                    }

                    try{
                        Collections.sort(list, object : Comparator<Plaatsen?> {
                            override fun compare(o1: Plaatsen?, o2: Plaatsen?): Int {
                                val k = o1!!.distance.toFloat() - o2!!.distance.toFloat()
                                return if (k > 0) {
                                    1
                                } else if (k == 0f) {
                                    0
                                } else {
                                    -1
                                }
                            }
                        })
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                    adapter.notifyDataSetChanged()
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
                        try{
                            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location : Location? ->
                                   getallData()
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
}