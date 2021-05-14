package it.uniupo.progetto

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uniupo.progetto.fragments.*
import kotlin.concurrent.thread
import kotlin.math.round
import kotlin.properties.Delegates

class GestoreActivity : AppCompatActivity() {
    fun stampaArray(array : ArrayList<Prodotto>){
        Log.d("totale","HomeActivity- array -> $array")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestore)
        val orderFragment = OrderFragment()
        val shopFragment = ShopFragment()
        val profileFragment = ProfileFragment()
        makeCurrentFragment(shopFragment)
        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.order -> {
                    makeCurrentFragment(orderFragment)
                }
                R.id.shop -> {
                    makeCurrentFragment(shopFragment)
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

    companion object{

        var array: ArrayList<Prodotto> = ArrayList()
        var carrello: ArrayList<Prodotto> = ArrayList()
        var tot by Delegates.observable(0.0){
                property, oldValue, newValue ->
            Log.d("TAG","New Value $newValue")
            Log.d("TAG","Old Value $oldValue")
            // HomeActivity().updateTot()
        }
    }
}

