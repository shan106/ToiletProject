package com.example.project

import android.content.Context
import android.content.SharedPreferences

class MySharedPref(var context: Context) {
    var sharedPreferences: SharedPreferences

    var appCount: Int
        get() = sharedPreferences.getInt(context.getString(R.string.app_count), 0)
        set(value) {
            val editor = sharedPreferences.edit()
            editor.putInt(context.getString(R.string.app_count), value).apply()
        }

    init {
        sharedPreferences =
            context.getSharedPreferences(context.getString(R.string.my_pref), Context.MODE_PRIVATE)
    }
}