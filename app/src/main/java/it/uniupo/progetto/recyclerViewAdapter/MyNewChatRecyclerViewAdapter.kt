package it.uniupo.progetto.recyclerViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.DatiPersonali.Utente
import it.uniupo.progetto.R

class MyNewChatRecyclerViewAdapter(private var riders: ArrayList<Utente>) : RecyclerView.Adapter<MyNewChatRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_row, parent, false)
        val mail = view.findViewById<TextView>(R.id.mail)
        mail.setOnClickListener{
            //manda a chat con utente
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = riders[position]
        holder.name.text = item.nome
        holder.surname.text = item.cognome
        holder.mail.text = item.email
    }

    override fun getItemCount(): Int = riders.size
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var surname: TextView = view.findViewById(R.id.surname)
        var name : TextView = view.findViewById(R.id.name)
        var mail : TextView = view.findViewById(R.id.mail)
    }

}
