package it.uniupo.progetto.fragments

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import it.uniupo.progetto.Consegna
import it.uniupo.progetto.R
import it.uniupo.progetto.recyclerViewAdapter.*
import java.io.IOException
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class Rider_ConsegneFragment : Fragment() {

    lateinit var viewConsegne: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        viewConsegne = inflater.inflate(R.layout.fragment_consegne_list, container, false)
        val recyclerView = viewConsegne.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(viewConsegne.context)
        Log.d("consegne", "dentro rider")
        getDelivery(
                object : myCallback {
                    override fun onCallback(consegne: MutableList<Consegna>) {
                        Log.d("consegne", "consegne vale $consegne")
                        recyclerView.adapter = MyConsegneRecyclerViewAdapter(consegne)
//                        Log.d("RISULTATO", consegne.toString())
                    }
                })


        //switch per rendere un rider disponibile
        val delivery_switch = viewConsegne.findViewById<SwitchCompat>(R.id.switch_delivery)
        getDisponibilita(object : myCallbackBoolean{
            override fun onCallback(ris: Boolean) {
                delivery_switch.isChecked=!ris
            }
        })

        delivery_switch.setOnClickListener{
            val status = delivery_switch.isChecked
            changeStatus(status)
        }


        return viewConsegne
    }

    private fun getDisponibilita(mycallback: myCallbackBoolean) {
        val db = FirebaseFirestore.getInstance()
        val rider = FirebaseAuth.getInstance().currentUser!!.email.toString()
        db.collection("riders").document(rider).get()
                .addOnSuccessListener {
                    val occ = it.getBoolean("occupato")
                    mycallback.onCallback(occ!!)
                }
    }

    interface myCallback{
        fun onCallback(consegne: MutableList<Consegna>){
        }
    }
    interface myCallbackBoolean{
        fun onCallback(ris: Boolean){
        }
    }

    fun getDelivery(myCallback: myCallback) {
        val market = Location("")

        market.latitude = 44.994154
        market.longitude = 8.565942

        val db = FirebaseFirestore.getInstance()
        val rider = FirebaseAuth.getInstance().currentUser!!.email.toString()
        var consegne = arrayListOf<Consegna>()
        db.collection("delivery").document(rider).collection("orders").get()
                .addOnCompleteListener {
                    for(order in it.result){
                        val ordId = order.getString("orderId").toString()
                        Log.d("mattia","riempimento liste: " + ordId)
                        val client = order.getString("client").toString()
                        val stato = order.getLong("stato")!!.toInt()
                        val distanza = order.getDouble("distanza")!!
                        val tipo_pagamento = order.getString("tipo_pagamento").toString()
                        var posizione = ""
                        db.collection("users").document(client).get()
                                .addOnSuccessListener {
                                    posizione = it.getString("address").toString()
                                    val consegna = Consegna(client,null,posizione,tipo_pagamento,stato,ordId,distanza,rider)
                                    Log.d("consegne","consegna vale $consegna")
                                    consegne.add(consegna)
                        myCallback.onCallback(consegne)
                                }
                    }
                    }
    }
    //rende disponibile il rider al gestore
    fun changeStatus(switch: Boolean){
        val user = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String,Any>(
                "disponibile" to switch
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
