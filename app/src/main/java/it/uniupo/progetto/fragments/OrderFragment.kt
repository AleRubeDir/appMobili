
package it.uniupo.progetto.fragments
import it.uniupo.progetto.recyclerViewAdapter.*
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
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderFragment  : Fragment() {
    lateinit var myview : View
   // val ords = arrayListOf<Order>()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        myview = inflater.inflate(R.layout.order_fragment, container, false)
        val history = myview.findViewById<RelativeLayout>(R.id.history)


        val arrivo = myview.findViewById<RelativeLayout>(R.id.arrivo)


        val orderView = myview.findViewById<RecyclerView>(R.id.list)
        val historyView = myview.findViewById<RecyclerView>(R.id.storico_ordini)
        arrivo!!.setOnClickListener {
            historyView.visibility = View.INVISIBLE
            if(  orderView.visibility == View.VISIBLE) orderView.visibility = View.INVISIBLE
            else orderView.visibility = View.VISIBLE
            getOrders(object : MyCallbackConsegne {
                override fun onCallback(order: ArrayList<Consegna>) {
                    for (o in order) Log.d("gestore_", "$o \n")
                    orderView.adapter = MyGestoreConsegneRecyclerViewAdapter(order)
                }
            })
        }

        history!!.setOnClickListener {
            orderView.visibility = View.INVISIBLE
            if(  historyView.visibility == View.VISIBLE) historyView.visibility = View.INVISIBLE
            else historyView.visibility = View.VISIBLE
                    getOrderInfo(object : MyCallback {
                        override fun onCallback(ords: ArrayList<Order>) {
                            historyView.adapter = MyHistoryOrderAdapter(ords)
                }
            })

        }
        return myview
    }

private fun getOrderInfo(myCallback: MyCallback){
    val db = FirebaseFirestore.getInstance()
    val ords = arrayListOf<Order>()
        db.collection("orders_history").get()
                    .addOnCompleteListener {
                        for (d in it.result) {
                                    val u = d.getString("mail").toString()
                                    val rider = d.getString("rider").toString()
                                    val tipo = d.getString("tipo").toString()
                                    val data = d.getTimestamp("data")!!.seconds
                                    val ratingQ = d.getLong("ratingQ")!!.toInt()
                                    val ratingV = d.getLong("ratingV")!!.toInt()
                                    val ratingC = d.getLong("ratingC")!!.toInt()
                                    val tot = d.getString("tot").toString()
                                    val ord = Order(d.id, u, rider, tipo, arrayListOf(), ratingQ, ratingV, ratingC, convertLongToTime(data), tot, 0)
                                    Log.d("history", "ord vale $ord")
                                    ords.add(ord)
                        }
                    myCallback.onCallback(ords)
                    }
        }

    /*  ORDINI IN ARRIVO  */
    private fun getOrders(myCallbackOrders: MyCallbackConsegne) {
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
                                    geocodeMatches = Geocoder(myview.context).getFromLocationName(indirizzo, 1)
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

    /* FINE ORDINI IN ARRIVO */
    fun convertLongToTime(time: Long): String {
        val date = Date(time*1000)
        //  Log.d("mess","time vale $time date vale $date")
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }
    interface MyCallbackConsegne{
        fun onCallback(order: ArrayList<Consegna>)
    }
    interface MyCallback {
        fun onCallback(ords: ArrayList<Order>)
    }

    interface MyCallback2 {
        fun onCallback(cods: ArrayList<String>)
    }
    interface MyCallbackProds {
        fun onCallback(prods: ArrayList<Prodotto>)
    }
    interface MyCallbackContatore {
        fun onCallback(size : Int )
    }
    class Order(var id : String?,var cliente : String, var rider : String , var tipo : String, var arr : ArrayList<Prodotto>, var ratingQ : Int = -1, var ratingV : Int = -1, var ratingC : Int = -1, var date : String, var tot : String , var richiamato : Int = 0) {
        override fun toString(): String {
            return "Tipo : $tipo \n ratingQ : $ratingQ \n ratingV : $ratingV \n ratingC : $ratingC \n date : $date \n arr : $arr "
        }
    }
}
