package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.fragments.MyOrderActionsAdapter
import it.uniupo.progetto.fragments.ProfileFragment.Azione

class Ordini : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    /*    setContentView(R.layout.ordini)
        val track = findViewById<TextView>(R.id.track)
        val chat = findViewById<TextView>(R.id.chat)
        val ritira = findViewById<TextView>(R.id.ritira)
        val orders = findViewById<TextView>(R.id.past_orders)

        track.setOnClickListener{
            startActivity(Intent(this, RiderPosition::class.java))
        }
        chat.setOnClickListener{

        }
        ritira.setOnClickListener{

        }
        orders.setOnClickListener{

        }*/
        setContentView(R.layout.fragment_my_orders)
        //       // Log.d("login", "---- ${requireActivity().getSharedPreferences("login", 0).getString("login", "")}")
        val recyclerView = findViewById<RecyclerView>(R.id.profile_actions)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        var array = ArrayList<Azione>()

        array.add(Azione("Traccia il tuo ordine", 0))
        array.add(Azione("Chat con rider", 1))
        array.add(Azione("Richiama ordine", 2))
        array.add(Azione("Storico degli ordini", 3))
        array.sortBy { it.id }

        recyclerView.adapter = MyOrderActionsAdapter(array)

    }
}