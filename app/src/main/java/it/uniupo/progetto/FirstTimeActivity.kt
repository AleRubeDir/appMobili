package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uniupo.progetto.fragments.Customer
import it.uniupo.progetto.fragments.Rider
import it.uniupo.progetto.fragments.Gestore

class FirstTimeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first)
        val customer = Customer()
        val rider = Rider()
        val gestore = Gestore()
        val a = arrayOf(customer,rider,gestore)
        val mail = intent.getStringExtra("mail")!!
        val next = findViewById<Button>(R.id.next)
        val end = findViewById<Button>(R.id.end)
        var i = 0;
        makeCurrentFragment(a[i])
        next.setOnClickListener{
            println("dentro")
            i++
            makeCurrentFragment(a[i])
            if(i==2){
                    println("CIAO")
                    next.visibility = View.INVISIBLE
                    end.visibility = View.VISIBLE
            }
        }
        end.setOnClickListener {

            Log.d("google","mail in firsttime vale $mail")
            val intent = Intent(this, ChooseActivity::class.java)
            intent.putExtra("mail", mail!! )
            startActivity(intent)
        }

    }
    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        replace(R.id.fl_wrapper,fragment)
        commit()
    }
}