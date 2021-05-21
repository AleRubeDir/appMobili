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

class RiderActivity : AppCompatActivity() {
 fun stampaArray(array : ArrayList<Prodotto>){
        Log.d("totale","RiderActivity- array -> $array")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider)
        val chatFragment = CartListFragment()
        val deliveryFragment = Consegne_todo()
        val profileFragment = ProfileFragment()
        makeCurrentFragment(deliveryFragment)
        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.carrello -> {

                    thread(start = true) {
                        stampaArray(carrello)
                        makeCurrentFragment(chatFragment)
                    }

                    Log.d("totale","Tot in home vale $tot")
                }
                R.id.profilo -> {
                    makeCurrentFragment(deliveryFragment)
                }
                R.id.shop -> {
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

