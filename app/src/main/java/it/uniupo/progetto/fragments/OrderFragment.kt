
package it.uniupo.progetto.fragments
import it.uniupo.progetto.recyclerViewAdapter.*
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import it.uniupo.progetto.R
import it.uniupo.progetto.StoricoOrdini
import it.uniupo.progetto.fragments.ProfileFragment.Azione
class OrderFragment  : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.order_fragment, container, false)
        var array = ArrayList<Azione>()
/*        array.add(Azione("Traccia il tuo ordine", 0))
        array.add(Azione("Chat con rider", 1))
        array.add(Azione("Richiama ordine", 2))
        array.add(Azione("Storico degli ordini", 3))*/
        // Inflate the layout for this fragment

        val history = view?.findViewById<RelativeLayout>(R.id.history)
        history!!.setOnClickListener{
            startActivity(Intent(view.context,StoricoOrdini::class.java))
        }
        return view

    }
}
