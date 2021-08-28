package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uniupo.progetto.fragments.*
import kotlin.concurrent.thread
import kotlin.math.round
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class GestoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestore)
        val orderFragment = OrderFragment()
        val shopFragment = ShopFragment()
        val profileFragment = ProfileFragment()
        makeCurrentFragment(shopFragment)
        val from_notification = intent.getStringExtra("from_notification")
        if(from_notification!=null){
            makeCurrentFragment(orderFragment)
        }
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
        val add = findViewById<ImageButton>(R.id.add)
        add.setOnClickListener{
            startActivity(Intent(this,AddProduct::class.java))
        }
        val chat = findViewById<ImageButton>(R.id.chat)
        chat.setOnClickListener{
            makeCurrentFragment(ChatGestoreFragment())
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        replace(R.id.fl_wrapper,fragment)
        commit()
    }
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}

