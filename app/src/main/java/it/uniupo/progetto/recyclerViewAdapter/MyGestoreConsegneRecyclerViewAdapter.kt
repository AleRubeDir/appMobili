package it.uniupo.progetto.recyclerViewAdapter

import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.*
import it.uniupo.progetto.Consegna
import it.uniupo.progetto.R

class MyGestoreConsegneRecyclerViewAdapter(
        private val values: ArrayList<Consegna>
) : RecyclerView.Adapter<MyGestoreConsegneRecyclerViewAdapter.ViewHolder>() {
    lateinit var view : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_gestore_consegne, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
       // if (item.rider != ""){
                holder.indirizzo.text = item.posizione
            holder.tipo_pagamento.text = item.tipo_pagamento
            holder.distanza.text = view.context.getString(R.string.km, "%.2f".format(item.distanza))
            holder.userId.text = item.clientMail
            holder.orderId.text = item.orderId
            val consegna = view.findViewById<CardView>(R.id.order)
            consegna.setOnClickListener {
                val intent = Intent(view.context, AssegnaOrdine::class.java)
                intent.putExtra("ordId", item.orderId)
                intent.putExtra("client",item.clientMail)
                intent.putExtra("tipo",item.tipo_pagamento)
                intent.putExtra("distanza",item.distanza)
                view.context.startActivity(intent)
            }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val indirizzo: TextView = view.findViewById(R.id.indirizzo)
        val tipo_pagamento: TextView = view.findViewById(R.id.pagamento)
        val distanza: TextView = view.findViewById(R.id.distanza)
        val userId: TextView = view.findViewById(R.id.userMail)
        val orderId: TextView = view.findViewById(R.id.orderId)

    }

}