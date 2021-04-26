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

        log.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))

        }
        reg.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}