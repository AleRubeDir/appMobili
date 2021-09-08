package it.uniupo.progetto

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uniupo.progetto.fragments.ProfileFragment
import it.uniupo.progetto.fragments.Rider_ConsegneFragment
import it.uniupo.progetto.fragments.Rider_chatFragment


class RiderActivity : AppCompatActivity() {
 fun stampaArray(array: ArrayList<Prodotto>){
        Log.d("totale", "RiderActivity- array -> $array")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider)
        val chatFragment = Rider_chatFragment()
        val deliveryFragment = Rider_ConsegneFragment()
        val profileFragment = ProfileFragment()
        makeCurrentFragment(deliveryFragment)
        Log.d("notifications","start notification service ")
        startService(Intent(this,NotificationService::class.java))
        // inizializza valori in caso di ordine accettato

        if(intent.getBooleanExtra("ordineAccettato",false)){
            ind = intent.getStringExtra("address")
            ordId = intent.getStringExtra("orderId")
            userMail = intent.getStringExtra("userMail")
            flag_consegna = 1
            makeCurrentFragment(Rider_ConsegneFragment())
        }


        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav.setOnNavigationItemReselectedListener {
            when(it.itemId){
                R.id.chat -> {
                    makeCurrentFragment(chatFragment)
                }
                R.id.consegne -> {
                    makeCurrentFragment(deliveryFragment)
                }
                R.id.profilo -> {
                    makeCurrentFragment(profileFragment)
                }
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        replace(R.id.fl_wrapper, fragment)
        commit()
    }
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    companion object {
        var flag_consegna = 0
        var ind : String? = null
        var ordId : String? = null
        var userMail : String? = null
    }

}

