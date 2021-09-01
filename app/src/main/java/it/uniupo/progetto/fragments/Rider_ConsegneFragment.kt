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
                    override fun onCallback(consegne: List<Consegna>) {
                        Log.d("consegne", "consegne vale $consegne")
                        recyclerView.adapter = MyConsegneRecyclerViewAdapter(consegne as MutableList<Consegna>)
//                        Log.d("RISULTATO", consegne.toString())
                    }
                })


        //switch per rendere un rider disponibile
        val delivery_switch = viewConsegne.findViewById<SwitchCompat>(R.id.switch_delivery)
        delivery_switch.setOnClickListener{
            val status = delivery_switch.isChecked
            changeStatus(status)
        }


        return viewConsegne
    }

    interface myCallback{
        fun onCallback(consegne: List<Consegna>){
        }
    }

    fun getDelivery(myCallback: myCallback) {
        val market = Location("")

        market.latitude = 44.994154
        market.longitude = 8.565942

        val db = FirebaseFirestore.getInstance()
        val rider = FirebaseAuth.getInstance().currentUser!!.email.toString()
        var consegne = arrayListOf<Consegna>()
        /*    db.collection("delivery").document(rider).collection("client").get()
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
                                        var geocodeMatches: List<Address>? = null
                                        try {
                                            geocodeMatches = Geocoder(viewConsegne.context).getFromLocationName(posizione, 1)
                                        } catch (e: IOException) {
                                            e.printStackTrace()
                                        }
                                        var cons_rider = Location("")

                                        for (mat in geocodeMatches!!) {
                                            cons_rider.latitude = mat.latitude
                                            cons_rider.longitude = mat.longitude
                                        }

                                        var distanza = (market.distanceTo(cons_rider)/1000).toDouble()
                                        var consegna = Consegna(clienti.id,null,posizione,tipo_pagamento,stato,orderId,distanza,rider)

                                        consegne.add(consegna)
                                    }
                                    myCallback.onCallback(consegne)
                                }
                        //db.collection("delivery").document(rider).collection("client").document(clienti.id).collection("products").get()
                        //Log.d("Consegne",clienti.id)
                    }
                }*/
        /*  db.collection("orders").get()
                .addOnSuccessListener {
                    for(d in it){
                        db.collection("orders").document(d.id).collection("order").get()
                                .addOnSuccessListener {
                                    for(ord in it){
                                        db.collection("orders").document(d.id).collection("order").document(ord.id).collection("details").document("dett").get()
                                                .addOnSuccessListener { x->
                                                    var posizione = x.getString("posizione").toString()
                                                    var stato = x.getString("stato").toString()
                                                    var tipo_pagamento = x.getString("tipo").toString()
                                                    var orderId = ord.id
                                                    var geocodeMatches: List<Address>? = null
                                                    try {
                                                        geocodeMatches = Geocoder(viewConsegne.context).getFromLocationName(posizione, 1)
                                                    } catch (e: IOException) {
                                                        e.printStackTrace()
                                                    }
                                                    var cons_rider = Location("")

                                                    for (mat in geocodeMatches!!) {
                                                        cons_rider.latitude = mat.latitude
                                                        cons_rider.longitude = mat.longitude
                                                    }

                                                    var distanza = (market.distanceTo(cons_rider)/1000).toDouble()
                                                    var consegna = Consegna(d.id,null,posizione,tipo_pagamento,stato,orderId,distanza,rider)

                                                    consegne.add(consegna)
                                                }
                                        myCallback.onCallback(consegne)
                                                }
                                    }
                                }
                    }
                }*/

        db.collection("delivery").document(rider).collection("orders").get()
                .addOnCompleteListener {
                    for(order in it.result){
                        var ordId = order.id
                        var client = order.getString("client").toString()
                        var stato = order.getString("stato").toString()
                        var lat = order.getDouble("lat")
                        var lon = order.getDouble("lon")
                        var tipo_pagamento = order.getString("tipo_pagamento").toString()
                        //giusta questa distanza??
                        var cons_rider = Location("")
                        cons_rider.longitude = lon!!
                        cons_rider.latitude = lat!!
                        var distanza = (market.distanceTo(cons_rider)/1000).toDouble()
                        var posizione = ""
                        db.collection("users").document(client).get()
                                .addOnSuccessListener {
                                    posizione = it.getString("address").toString()
                                    var consegna = Consegna(client,null,posizione,tipo_pagamento,stato,ordId,distanza,rider)
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
