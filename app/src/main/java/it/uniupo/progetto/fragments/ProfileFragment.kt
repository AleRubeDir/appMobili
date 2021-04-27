package it.uniupo.progetto.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.HomeActivity
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.ProfileActionsAdapter
import it.uniupo.progetto.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        if (view is RecyclerView) {
            with(view) {
                var array = ArrayList<Azione>()
                array.add(Azione("Dati personali",0))
                array.add(Azione("La mia posizione",1))
                array.add(Azione("I miei ordini",2))
                adapter = ProfileActionsAdapter(array)
            }
        }
        return view
    }


    class Azione(val nome : String, val id : Int){
        override fun toString():String{
            return "Azione eseguita : $nome"
        }
    }
}