package it.uniupo.progetto


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.fragments.*

class ClienteActivity : AppCompatActivity() {
    lateinit var notificationManager : NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val chat = findViewById<ImageButton>(R.id.chat)

        val cartFragment = CartListFragment()
        val profileFragment = ProfileFragment()
        val shopFragment = ItemFragment()
        if(intent.getStringExtra("cart")=="vai"){
            makeCurrentFragment(cartFragment)
        }
        makeCurrentFragment(shopFragment)

        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.carrello -> makeCurrentFragment(cartFragment)
                R.id.profilo -> makeCurrentFragment(profileFragment)
                R.id.shop -> makeCurrentFragment(shopFragment)
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        replace(R.id.fl_wrapper, fragment)
        commit()
    }

    override fun onBackPressed() {
        finish()
    }

    interface MyCallback {
        fun onCallback(ris : Boolean)
    }
    companion object    {
        var array: ArrayList<Prodotto> = ArrayList()
        var carrello: ArrayList<Prodotto> = ArrayList()
        var tot = 0.0

    }


}

