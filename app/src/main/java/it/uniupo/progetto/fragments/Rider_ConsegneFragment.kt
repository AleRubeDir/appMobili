package it.uniupo.progetto.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.Consegna
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R
import it.uniupo.progetto.recyclerViewAdapter.*
/**
 * A fragment representing a list of Items.
 */
class Rider_ConsegneFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_consegne_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        getDelivery(
                object : myCallback {
                    override fun onCallback(consegne: List<Consegna>) {

                        recyclerView.adapter = MyConsegneRecyclerViewAdapter(consegne)
                        Log.d("RISULTATO", consegne.toString())
                    }
                }
        )

        //switch per rendere un rider disponibile
        val delivery_switch = view.findViewById<SwitchCompat>(R.id.switch_delivery)
        delivery_switch.setOnClickListener{
            val status = delivery_switch.isChecked
            changeStatus(status)
        }

        return view
    }

    interface myCallback{
        fun onCallback(consegne: List<Consegna>){
        }
    }

    fun getDelivery(myCallback: myCallback){
        val db = FirebaseFirestore.getInstance()
        val rider = FirebaseAuth.getInstance().currentUser!!.email.toString()
        var consegne = arrayListOf<Consegna>()
        db.collection("delivery").document(rider).collection("client").get()
                .addOnSuccessListener {
                    result ->
                    for(clienti in result){
                        db.collection("delivery").document(rider).collection("client").document(clienti.id).collection("details").get()
                                .addOnSuccessListener {
                                    result ->
                                    for (x in result){
                                        var posizione = x.getString("posizione").toString()
                                        var stato = x.getString("stato").toString()
                                        var tipo_pagamento = x.getString("tipo_pagamento").toString()
                                        var orderId = x.id

                                        var consegna = Consegna(clienti.id,null,posizione,tipo_pagamento,stato,orderId)

                                        consegne.add(consegna)
                                    }
                                    myCallback.onCallback(consegne)
                                }
                        //db.collection("delivery").document(rider).collection("client").document(clienti.id).collection("products").get()
                        //Log.d("Consegne",clienti.id)
                    }
                }

    }

    //rende disponibile il rider al gestore
    fun changeStatus(switch: Boolean){
        val user = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String,Any>(
                "occupato" to if (switch ) 0 else 1
        )
        db.collection("riders").document(user).set(entry, SetOptions.merge())
    }


   /* private fun caricaProdotto(p: Prodotto) {
        val db = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String, Any?>(
                "id" to p.id,
                "titolo" to p.titolo,
                "desc" to p.desc,
                "img" to p.img,
                "qta" to p.qta,
                "prezzo" to p.prezzo,
        )
        db.collection("products").document(p.id.toString())
                .set(entry)
                .addOnSuccessListener {
                    Log.d("add", "Aggiunto prodotto $entry")
                }
                .addOnFailureListener{ e -> Log.w("---", "Errore aggiunta prodotto", e)}*/


}