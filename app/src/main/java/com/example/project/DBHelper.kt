package com.example.project

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat


val DATABASE_NAME = "DBtoilet"
val TABLE_NAME = "Plaatsen"
val KEY_ID = "id"
val ADRES = "adres"
val GESLACHT = "geslacht"
val ROLSTOEL = "rolstoel"
val LUIERTAFEL = "luierstoel"
val LONGE = "longe"
val LAT = "lat"

class DBHelper(private val context: Context, ) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {


    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + "  (" +
        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        ADRES + " VARCHAR(256), " +
        GESLACHT + " VARCHAR(10), " +
        ROLSTOEL + " BOOLEAN, " +
        LUIERTAFEL+ " BOOLEAN, " +
        LONGE + " FLOAT, " +
        LAT + " FLOAT)";
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(plaatsen : Plaatsen){
        val db = this.writableDatabase
        val conV = ContentValues()
        conV.put(ADRES, plaatsen.adres)
        conV.put(GESLACHT, plaatsen.geslacht)
        conV.put(ROLSTOEL, plaatsen.rolstoel)
        conV.put(LUIERTAFEL, plaatsen.luiertafel)
        conV.put(LONGE, plaatsen.long)
        conV.put(LAT, plaatsen.lat)
        val result = db.insert(TABLE_NAME, null, conV)
        Log.e("***test","rolstoel "+plaatsen.rolstoel+" luiertafel "+plaatsen.luiertafel)
//        if (result == (-1).toLong())
//            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
//        else
//            Toast.makeText(context, "Succes", Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("Range")
    fun getData(): ArrayList<Plaatsen> {

        val list = ArrayList<Plaatsen>()
        var conV = ContentValues()
        val db = this.readableDatabase
        val query = "select * from $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        cursor.moveToFirst()
        var i = 0
        while (cursor.moveToNext()) {
            val data = Plaatsen()
            data.id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
            data.adres = cursor.getString(cursor.getColumnIndex(ADRES))
            data.geslacht = cursor.getString(cursor.getColumnIndex(GESLACHT))
            data.luiertafel = !cursor.getString(cursor.getColumnIndex(LUIERTAFEL)).equals("0")
            data.rolstoel = !cursor.getString(cursor.getColumnIndex(ROLSTOEL)).equals("0")
            data.lat = cursor.getDouble(cursor.getColumnIndex(LAT))
            data.long = cursor.getDouble(cursor.getColumnIndex(LONGE))
            Log.e("***test","rolstoel "+data.rolstoel+" luiertafel "+data.luiertafel + " "+ ++i)
            list.add(data)
        }
        Log.e("***test","in db  "+list.size)
        return list
    }

    fun delete(id: String): Int {
        val db = this.writableDatabase
        // Deleting Row
        val success = db.delete(TABLE_NAME, KEY_ID + "=" + id, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}



