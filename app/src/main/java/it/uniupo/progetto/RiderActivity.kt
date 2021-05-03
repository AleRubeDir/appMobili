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
        Log.d("totale","HomeActivity- array -> $array")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider)
        val chatFragment = CartListFragment()
        val deliveryFragment = Consegne_todo()
        val profileFragment = ProfileFragment()
        cart_hide()
        makeCurrentFragment(deliveryFragment)
        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.carrello -> {

                    thread(start = true) {
                        stampaArray(carrello)
                        makeCurrentFragment(chatFragment)
                    }
                    cart_show()
                    updateTot()
                    Log.d("totale","Tot in home vale $tot")
                }
                R.id.profilo -> {
                    cart_hide()
                    makeCurrentFragment(deliveryFragment)
                }
                R.id.shop -> {
                    cart_hide()
                    makeCurrentFragment(profileFragment)
                }
            }
            true
        }

    }

    fun updateTot(){
        val totview = findViewById<TextView>(R.id.tot)
        var totdoub = "%.2f".format(tot)
        totview.text = getString(R.string.cash,totdoub)
    }
    private fun cart_show(){
        val tot = findViewById<RelativeLayout>(R.id.tot_layout)
        tot.visibility = View.VISIBLE
    }
    private fun cart_hide(){
        val tot = findViewById<RelativeLayout>(R.id.tot_layout)
        tot.visibility = View.INVISIBLE
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

