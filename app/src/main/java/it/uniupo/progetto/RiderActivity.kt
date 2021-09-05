package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
        val deliveryFragment = Rider_ConsegneFragment("", "", "", 1)
        val profileFragment = ProfileFragment()
        makeCurrentFragment(deliveryFragment)
        Log.d("notifications","start notification service ")
        startService(Intent(this,NotificationService::class.java))

        if(intent.getBooleanExtra("ordineAccettato",false)){
            val ind = intent.getStringExtra("address")
            val ordId = intent.getStringExtra("orderId")
            val userMail = intent.getStringExtra("userMail")
            makeCurrentFragment(Rider_ConsegneFragment(ind!!,ordId!!,userMail!!,1))
        }


        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav.setOnNavigationItemSelectedListener {
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

}

