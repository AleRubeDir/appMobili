
package it.uniupo.progetto.fragments
import it.uniupo.progetto.recyclerViewAdapter.*
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.*
import it.uniupo.progetto.fragments.ProfileFragment.Azione
import java.io.IOException

class OrderFragment  : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.order_fragment, container, false)
        val history = view?.findViewById<RelativeLayout>(R.id.history)
        history!!.setOnClickListener{
            startActivity(Intent(view.context,StoricoOrdini::class.java))
        }

        val orderView = view.findViewById<RecyclerView>(R.id.list)

        getOrders(object: MyCallbackOrders{
            override fun onCallback(order: ArrayList<Consegna>) {
                for (o in order) Log.d("gestore_", "$o \n")
                orderView.adapter = MyGestoreConsegneRecyclerViewAdapter(order)

            }
        })


        return view

    }

    private fun getOrders(myCallbackOrders: MyCallbackOrders) {
        val db = FirebaseFirestore.getInstance()
        var tipo_pagamento = ""
        val prodotti = arrayListOf<Prodotto>()
        var rider = ""
        val orders = arrayListOf<Consegna>()
        Log.d("gestore_","1")
        getClients(object : NotificationService.MyCallback {
            override fun onCallback(ris: List<String>) {
                Log.d("gestore_","2")
                for (cliente in ris) {
                    db.collection("users").document(cliente).get()
                            .addOnSuccessListener {
                                var indirizzo = it.getString("address").toString()
                    getOrderByClients(cliente, object : NotificationService.MyCallback {
                        override fun onCallback(ris: List<String>) {
                            Log.d("gestore_","3")
                            for (ord in ris) {
                                tipo_pagamento = ""
                                prodotti.clear()
                                rider = ""
                                db.collection("orders").document(cliente).collection("order").document(ord).collection("details").document("dett").get()
                                        .addOnSuccessListener {
                                            tipo_pagamento = it.getString("tipo").toString()
                                        }
                                db.collection("orders").document(cliente).collection("order").document(ord).collection("products").get()
                                        .addOnSuccessListener {
                                            for (d in it) {
                                                var p = Prodotto(d.getLong("id")!!.toInt(), "", d.getString("titolo").toString(), "", d.getString("prezzo").toString(), d.getLong("qta")!!.toInt())
                                                prodotti.add(p)
                                            }
                                        }
                                db.collection("orders").document(cliente).collection("order").document(ord).collection("rider").document("r").get()
                                        .addOnSuccessListener {
                                            rider = it.getString("mail").toString()
                                        }
                                val market = Location("")
                                market.latitude =  44.994154
                                market.longitude =   8.565942
                                var geocodeMatches: List<Address>? = null
                                try {
                                    geocodeMatches = Geocoder(view!!.context).getFromLocationName(indirizzo, 1)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                val cons_rider = Location("")

                                for (mat in geocodeMatches!!) {
                                    cons_rider.latitude = mat.latitude
                                    cons_rider.longitude = mat.longitude
                                }

                                var distanza = (market.distanceTo(cons_rider)/1000).toDouble()
                                var consegna = Consegna(cliente, prodotti, indirizzo, tipo_pagamento, "stato", ord,distanza)
                                orders.add(consegna)
                            }
                            myCallbackOrders.onCallback(orders)
                        }
                    })
                  }
                }
            }
        })
      //  myCallbackOrders.onCallback(orders)
    }
    private fun getClients(myCallback: NotificationService.MyCallback) {
        val db = FirebaseFirestore.getInstance()
        var ris = mutableListOf<String>()
        db.collection("orders").get()
                .addOnSuccessListener {
                    for (d in it){
                    ris.add(d.id)
                }
                    myCallback.onCallback(ris)
                }
    }

    private fun getOrderByClients(client : String,myCallback: NotificationService.MyCallback){
        val db = FirebaseFirestore.getInstance()
        var ris = mutableListOf<String>()
        db.collection("orders").document(client).collection("order").get()
                .addOnSuccessListener {
                    for (d in it){
                        ris.add(d.id)
                    }
                    myCallback.onCallback(ris)
                }
    }


    interface MyCallbackOrders{
        fun onCallback(order: ArrayList<Consegna>)
    }


}
