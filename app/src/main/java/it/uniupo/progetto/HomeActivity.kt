package it.uniupo.progetto

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.*
import kotlin.concurrent.thread
import kotlin.math.round
import kotlin.properties.Delegates

class HomeActivity : AppCompatActivity() {
    fun stampaArray(array : ArrayList<Prodotto>){
        Log.d("totale","HomeActivity- array -> $array")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        //createCart()
        val cartFragment = CartListFragment()
        val profileFragment = ProfileFragment()
        val shopFragment = ItemFragment()
        /*cart_hide()*/
        if(intent.getStringExtra("cart")=="vai"){
            makeCurrentFragment(cartFragment)
        }
        makeCurrentFragment(shopFragment)
       /* val compra = findViewById<Button>(R.id.compra)
        compra.setOnClickListener{
            
        }*/
        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.carrello -> {
                    thread(start = true) {
                        stampaArray(carrello)
                        makeCurrentFragment(cartFragment)
                    }
                  /*  cart_show()*/
                    prof_hide()/**/
                  /*  updateTot()*/
                    Log.d("totale","Tot in home vale $tot")
                }
                R.id.profilo -> {
                    thread(start = true) {
                        makeCurrentFragment(profileFragment)
                    }
                    /*cart_hide()*/
                    prof_show()
                }
                R.id.shop -> {
                    prof_hide()
                    /*cart_hide()*/
                    makeCurrentFragment(shopFragment)
                }
            }
            true
        }

    }
    private fun createCart(){

        val db = FirebaseFirestore.getInstance()
        var auth = FirebaseAuth.getInstance();
        val user = auth.currentUser!!.email
        Log.d("qta","Inizio nuovo carrello utente $user")
        val entry = hashMapOf<String, Any?>(
                "id" to "",
                "titolo" to "",
                "prezzo" to "",
                "qta" to "",
        )
        var x = false
        db.collection("carts").get()
                .addOnSuccessListener { result ->
                    for(doc in result ){
                        Log.d("qta","${doc.id} == $user")

                        if (doc.id == user){
                            Log.d("qta","Carrello già esistente")
                            x = true
                        }
                    }
                    if(!x){
                        Log.d("qta","Creato nuovo carrello")
                        db.collection("carts").document(user!!).collection("products")
                    }

                }
    }
    /*fun updateTot(){
        val totview = findViewById<TextView>(R.id.tot)
        var totdoub = "%.2f".format(tot)
        totview.text = getString(R.string.cash,totdoub)
    }*/
   /* private fun cart_show(){
        val tot = findViewById<RelativeLayout>(R.id.tot_layout)
        tot.visibility = View.VISIBLE
    }*/
    private fun prof_show(){
        val tot = findViewById<RelativeLayout>(R.id.layout_profilo)
        tot.visibility = View.VISIBLE
    }
    private fun prof_hide(){
        val tot = findViewById<RelativeLayout>(R.id.layout_profilo)
        tot.visibility = View.INVISIBLE
    }

   /* private fun cart_hide(){
        val tot = findViewById<RelativeLayout>(R.id.tot_layout)
        tot.visibility = View.INVISIBLE
    }*/
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
        //welldone
       // HomeActivity().updateTot()
    }


}


}

