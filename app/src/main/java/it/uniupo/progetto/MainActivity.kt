package it.uniupo.progetto

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val rel1 = findViewById<RelativeLayout>(R.id.rel1)
        val rel2 = findViewById<RelativeLayout>(R.id.animation)
        val riderImage = findViewById<ImageView>(R.id.rider_animation)
            Log.d("animation", "cliccato")
            riderImage.animate().apply{
                duration = 2100
                xBy(width.toFloat())
            }.withEndAction{
                rel2.visibility = View.INVISIBLE
                rel1.visibility = View.VISIBLE
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val log = findViewById<Button>(R.id.btn_log)
        val reg = findViewById<Button>(R.id.btn_reg)
        val sp = applicationContext.getSharedPreferences("login", 0)
        val mail = sp.getString("login", "")
        if(mail!="null") {
            Log.d("mail", "Entro")
            when (mail) {
                "Cliente" -> {
                    Log.d("mail", "account cliente")
                    startActivity(Intent(this, ClienteActivity::class.java))
                }
                "Rider" -> {
                    Log.d("mail", "account rider")
                    startActivity(Intent(this, RiderActivity::class.java))
                }
                "Gestore" -> {
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