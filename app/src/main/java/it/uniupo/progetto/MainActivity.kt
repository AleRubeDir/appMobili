package it.uniupo.progetto

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.ProfileFragment
import it.uniupo.progetto.fragments.ShopFragment

class MainActivity : AppCompatActivity() {
    val PREF_USER_NAME = "username"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val log = findViewById<Button>(R.id.btn_log)
        val reg = findViewById<Button>(R.id.btn_reg)
    /*    val accedi = findViewById<Button>(R.id.accedi)
        val scelta = findViewById<Button>(R.id.scelta)*/
        var sp = applicationContext.getSharedPreferences("login",0)
        var mail = sp.getString("login","")
        Log.d("mail","mail vale $mail")
        if(mail!="null") {
            Log.d("mail","Entro")
            when {
                mail == "Cliente" -> {
                    Log.d("mail", "account cliente")
                    startActivity(Intent(this, HomeActivity::class.java))
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
            /*val db = FirebaseFirestore.getInstance()
            db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            Log.d("mail", "${document.get("type")}")
                            if (document.get("mail").toString() == mail && document.get("type").toString().isNotEmpty()) {
                                when {
                                    document.get("type").toString() == "Cliente" -> {
                                        Log.d("mail","${document.get("mail").toString()} - $mail - ${document.get("type").toString()} - account cliente")
                                        startActivity(Intent(this, HomeActivity::class.java))
                                    }
                                    document.get("type").toString() == "Rider" -> {
                                        Log.d("mail","account ${document.get("mail").toString()} - $mail - ${document.get("type").toString()} -  rider")
                                        startActivity(Intent(this, RiderActivity::class.java))
                                    }
                                    document.get("type").toString() == "Gestore" -> {
                                        Log.d("mail","account gestore")
                                        startActivity(Intent(this, GestoreActivity::class.java))
                                    }
                                }
                            }
                        }
                    }*/
        }
     /*   scelta.setOnClickListener{
            startActivity(Intent(this, ChooseActivity::class.java))
        }
        accedi.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }*/
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