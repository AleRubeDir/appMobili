package it.uniupo.progetto


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uniupo.progetto.fragments.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
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
                R.id.carrello ->  makeCurrentFragment(cartFragment)
                R.id.profilo ->  makeCurrentFragment(profileFragment)
                R.id.shop -> makeCurrentFragment(shopFragment)
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        replace(R.id.fl_wrapper,fragment)
        commit()
    }

    companion object    {
        var array: ArrayList<Prodotto> = ArrayList()
        var carrello: ArrayList<Prodotto> = ArrayList()
        var tot = 0.0
    }


}

