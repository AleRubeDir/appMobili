package it.uniupo.progetto.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.Consegna
import it.uniupo.progetto.R
import it.uniupo.progetto.recyclerViewAdapter.*
/**
 * A fragment representing a list of Items.
 */
class Rider_ConsegneFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                                        var consegna = Consegna(clienti.id,null,posizione,tipo_pagamento,stato)
                                        consegne.add(consegna)
                                    }
                                    myCallback.onCallback(consegne)
                                }
                        //db.collection("delivery").document(rider).collection("client").document(clienti.id).collection("products").get()

                        //Log.d("Consegne",clienti.id)
                    }
                }

    }


}