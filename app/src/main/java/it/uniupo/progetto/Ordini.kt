package it.uniupo.progetto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.recyclerViewAdapter.*
import it.uniupo.progetto.fragments.ProfileFragment.Azione

class Ordini : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_my_orders)
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