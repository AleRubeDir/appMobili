package it.uniupo.progetto.recyclerViewAdapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.AssegnaOrdine
import it.uniupo.progetto.GestoreActivity
import it.uniupo.progetto.R

class MySelectRiderRecyclerViewAdapter(var distanza : Double , var riders: ArrayList<AssegnaOrdine.Rider>, var ordId: String?, var clientMail : String?, var tipoPagamento : String?) : RecyclerView.Adapter<MySelectRiderRecyclerViewAdapter.ViewHolder>() {
    lateinit var view : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.rider_list, parent, false)

        return ViewHolder(view)
    }
    private fun scegliRiderPerOrdine(ordId: String?, rider : String) {
        val db = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String,Any?>(
                "distanza" to distanza,
                "stato" to -1,
                "statoPagamento" to -1,
                "orderId" to ordId,
                "client" to clientMail,
                "tipo_pagamento" to tipoPagamento,
                "leftMM" to false
        )
        var add=""
        db.collection("users").document(clientMail!!).get()
            .addOnSuccessListener { document->
                add = document.getString("address").toString()
                val add1 = document.getString("address").toString()
                Log.d("DELIVERY", "add 1  vale: $add")
                Log.d("DELIVERY", "add 2  vale: $add1")

                val dummy = hashMapOf<String, Any?>(
                        "tipo" to tipoPagamento,
                        //indirizzo ordine
                        "indirizzo" to add1,
                        "cliente" to clientMail
                )
                Log.d("refuse", "Ordine appena salvato, $dummy")

                if (ordId != null) {
                    db.collection("assignedOrders").document(ordId).set(dummy)
                }

            }

        Log.d("DELIVERY", "add vale fuori : $add")

//        val dummy = hashMapOf<String, Any?>(
//                "tipo" to tipoPagamento,
//                //indirizzo ordine
//                "indirizzo" to add,
//                "cliente" to clientMail
//        )
        val richiamato = hashMapOf<String, Any?>(
                "richiamato" to false,
        )

        Log.d("assegna","rider vale $rider ordId vale $ordId")
        db.collection("delivery").document(rider).set(richiamato, SetOptions.merge())
        db.collection("delivery").document(rider).collection("orders").document(ordId!!).set(entry, SetOptions.merge())
        val entry2 = hashMapOf<String, Any>(
                "mail" to rider
        )
        db.collection("orders").get().addOnCompleteListener {
            for(cliente in it.result){
                db.collection("orders").document(cliente.id).collection("order").document(ordId).collection("rider").document("r").set(entry2, SetOptions.merge())
                        }
                    Toast.makeText(view.context,"Proposta inviata al rider",Toast.LENGTH_SHORT).show()
                    view.context.startActivity(Intent(view.context , GestoreActivity::class.java))
                }
        db.collection("toassignOrders").document(ordId).delete()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = riders[position]
        Log.d("assegna","mail bind ${item.mail
        }")
        holder.nome.text = item.nome
        holder.cognome.text = item.cognome
        holder.distanza.text = view.context.getString(R.string.km, "%.2f".format(item.distanza))
        holder.mail.text = item.mail

        val scelta = view.findViewById<RelativeLayout>(R.id.rider)
        scelta.setOnClickListener{
            scegliRiderPerOrdine(ordId,item.mail)

        }

    }

    override fun getItemCount():Int = riders.size


    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var nome : TextView = view.findViewById(R.id.nome)
        var cognome : TextView = view.findViewById(R.id.cognome)
        var distanza : TextView = view.findViewById(R.id.distanza)
        var mail : TextView = view.findViewById(R.id.mail)
    }
}
