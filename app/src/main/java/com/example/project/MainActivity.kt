package com.example.project


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.database.sqlite.SQLiteDatabase

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.OverlayItem
import java.io.File
import java.net.URL


class MainActivity : AppCompatActivity() {

    private var float_btn : FloatingActionButton? = null
    var isOpen = false
    private lateinit var database: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val db = DBHelper(this)
        val z = assets.open("openbaar_toilet.geojson")
        JsonServ.getData(assets.open("openbaar_toilet.geojson"))
            .forEach { db.insertData(it) }


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = ToiletAdapter(this@MainActivity,db.getData())
        recyclerView.adapter = adapter

        val btn_click = findViewById<Button>(R.id.button)
        btn_click.setOnClickListener{
            val intent = Intent(this, com.example.project.Map::class.java)
            startActivity(intent)
        }

        val fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        val fabClosed = AnimationUtils.loadAnimation(this, R.anim.fab_closed)
        val fabRClock = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise)
        val fabanticlock = AnimationUtils.loadAnimation(this, R.anim.rotate_antclock)

        val filterbtn = findViewById<FloatingActionButton>(R.id.filter)
        val handicap = findViewById<FloatingActionButton>(R.id.handicap)
        val man_vrouw = findViewById<FloatingActionButton>(R.id.humanfilter)
        val baby = findViewById<FloatingActionButton>(R.id.baby)

        filterbtn.setOnClickListener{
            if (isOpen){
                handicap.startAnimation(fabClosed)
                man_vrouw.startAnimation(fabClosed)
                baby.startAnimation(fabClosed)
                filterbtn.startAnimation(fabRClock)
                isOpen = false
            }
            else{
                handicap.startAnimation(fabOpen)
                man_vrouw.startAnimation(fabOpen)
                baby.startAnimation(fabOpen)
                filterbtn.startAnimation(fabanticlock)
                handicap.isClickable
                man_vrouw.isClickable
                baby.isClickable
                isOpen =true
        }
//        handicap.setOnClickListener{
//            val data = mutableListOf<Plaatsen>()
//            database = db.writableDatabase
//            val cursor = database.rawQuery("SELECT ROLSTOEL FROM Plaatsen", null)
//            while (cursor.moveToNext()){
//                val place = Plaatsen()
//                    cursor.getString(cursor.getColumnIndex("rolstoel"))
//
//
//            }
//
//
//
//        }
        man_vrouw.setOnClickListener{

        }

        baby.setOnClickListener{

        }

        }
        }


    }

