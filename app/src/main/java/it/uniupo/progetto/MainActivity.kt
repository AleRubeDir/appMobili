package it.uniupo.progetto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import it.uniupo.progetto.fragments.ShopFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val log = findViewById<Button>(R.id.btn_log)
        val reg = findViewById<Button>(R.id.btn_reg)
        val accedi = findViewById<Button>(R.id.accedi)
        val scelta = findViewById<Button>(R.id.scelta)
        scelta.setOnClickListener{
            startActivity(Intent(this, ChooseActivity::class.java))
        }
        accedi.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }
        log.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        reg.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        val rider = findViewById<Button>(R.id.btn_rider)
        rider.setOnClickListener{
            startActivity(Intent(this,RiderActivity::class.java))
        }
    }
}