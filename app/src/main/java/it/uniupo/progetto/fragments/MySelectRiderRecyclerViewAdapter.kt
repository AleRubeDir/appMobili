package it.uniupo.progetto.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.R

class MySelectRiderRecyclerViewAdapter(var riders: ArrayList<OrderFragment.Rider>) : RecyclerView.Adapter<MySelectRiderRecyclerViewAdapter.ViewHolder>() {
    lateinit var view : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gestore_receive_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = riders[position]
        Log.d("distance","riders vale $riders")
        holder.nome.text = item.nome
        holder.cognome.text = item.cognome
        holder.distanza.text = view.context.getString(R.string.km, "%.2f".format(item.distanza))
    }

    override fun getItemCount():Int = riders.size


    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var nome : TextView = view.findViewById(R.id.nome)
        var cognome : TextView = view.findViewById(R.id.cognome)
        var distanza : TextView = view.findViewById(R.id.distanza)
    }
}
