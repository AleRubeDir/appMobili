package it.uniupo.progetto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    val PREF_USER_NAME = "username"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val log = findViewById<Button>(R.id.btn_log)
        val reg = findViewById<Button>(R.id.btn_reg)
        var sp = applicationContext.getSharedPreferences("login",0)
        var mail = sp.getString("login","")
        Log.d("mail","mail vale $mail")
        if(mail!="null") {
            Log.d("mail","Entro")
            when {
                mail == "Cliente" -> {
                    Log.d("mail", "account cliente")
                    startActivity(Intent(this, ClienteActivity::class.java))
                }
                mail == "Rider" -> {
                    Log.d("mail", "account rider")
                    startActivity(Intent(this, RiderActivity::class.java))
                }
                mail == "Gestore" -> {
                    Log.d("mail", "account gestore")
                    startActivity(Intent(this, GestoreActivity::class.java))
                }
            }

        }

        log.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        reg.setOnClickListener{

            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }



}