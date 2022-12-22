package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.project.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.login.setOnClickListener {
            if(loginValid()) {
                startActivity(Intent(this@LoginActivity,NewMainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this@LoginActivity, "Credentials not valid", Toast.LENGTH_SHORT).show()
            }
        }

        val mySharedPref = MySharedPref(this)
        Log.e("***Value","mySharedPref.appCount  "+mySharedPref.appCount )
        if(mySharedPref.appCount == 0){
            Log.e("***Value","mySharedPref.appCount  "+mySharedPref.appCount )
            val db = DBHelper(this)
//            val z = assets.open("openbaar_toilet.geojson")
            JsonServ.getData(assets.open("openbaar_toilet.geojson"))
                .forEach { db.insertData(it) }
            mySharedPref.appCount += 1
        }
    }
    fun loginValid() : Boolean{
        val email = binding.emailEd.text.toString()
        val pass = binding.passwrodEd.text.toString()
        return if(email.equals("chuck_jhon@gmail.com") && pass.equals("testing1234"))
            true
        else if(email.equals("freed_qumby@gmail.com") && pass.equals("testingtesting"))
            true
        else email.equals("jhon_wishko@gmail.com") && pass.equals("test123test123")
    }


}