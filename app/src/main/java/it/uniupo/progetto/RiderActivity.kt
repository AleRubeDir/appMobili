package it.uniupo.progetto

import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uniupo.progetto.fragments.*
import kotlin.properties.Delegates

class RiderActivity : AppCompatActivity() {
 fun stampaArray(array : ArrayList<Prodotto>){
        Log.d("totale","RiderActivity- array -> $array")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider)
        val chatFragment = Rider_chatFragment()
        val deliveryFragment = Rider_ConsegneFragment()
        val profileFragment = ProfileFragment()
        makeCurrentFragment(deliveryFragment)
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
        replace(R.id.fl_wrapper,fragment)
        commit()
    }
    override fun onBackPressed() {
        finish()
    }
    companion object{

        var array: ArrayList<Prodotto> = ArrayList()
        var carrello: ArrayList<Prodotto> = ArrayList()
        var tot by Delegates.observable(0.0){
                property, oldValue, newValue ->
            Log.d("TAG","New Value $newValue")
            Log.d("TAG","Old Value $oldValue")
        }
    }
}

