
package it.uniupo.progetto.fragments
import android.app.NotificationManager
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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderFragment  : Fragment() {
    lateinit var myview : View
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        myview = inflater.inflate(R.layout.order_fragment, container, false)
        val history = myview.findViewById<RelativeLayout>(R.id.history)


        val arrivo = myview.findViewById<RelativeLayout>(R.id.arrivo)

        val nm = myview.context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()

        val orderView = myview.findViewById<RecyclerView>(R.id.list)
        val historyView = myview.findViewById<RecyclerView>(R.id.storico_ordini)
        arrivo!!.setOnClickListener {
            historyView.visibility = View.INVISIBLE
            if(  orderView.visibility == View.VISIBLE) orderView.visibility = View.INVISIBLE
            else orderView.visibility = View.VISIBLE
                    getOrders(object : MyCallbackConsegne {
                        override fun onCallback(order: ArrayList<Consegna>) {
                            Log.d("order", "order vale $order")
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
                            historyView.adapter = MyHistoryOrderAdapter(ords, "Gestore")
                }
            })

        }
        return myview
    }
    private fun getOrderInfo(mycallback: MyCallback){
        val db = FirebaseFirestore.getInstance()
        var prod = arrayListOf<Prodotto>()
        var ord : Order
        val ords = arrayListOf<Order>()
        db.collection("orders_history").get()
            .addOnCompleteListener {
                for(d in it.result) {
                    val id = d.id
                    val mail = d.getString("mail").toString()
                    val rider = d.getString("rider").toString()
                    val tipo = d.getString("tipoPagamento").toString()
                    val data = convertLongToTime(d.getTimestamp("data")?.seconds)
                    val ratingQ = d.getLong("ratingQ")?.toInt()
                    val ratingV = d.getLong("ratingV")?.toInt()
                    val ratingC = d.getLong("ratingC")?.toInt()
                    val ratingRC = d.getLong("ratingRC")?.toInt()
                    val ratingRP = d.getLong("ratingRP")?.toInt()
                    val risultatoOrdine = d.getLong("risultatoOrdine")?.toInt()
                    ord = Order(id, mail, rider, tipo, prod, ratingQ, ratingV, ratingC, ratingRC, ratingRP, data, "0",0, risultatoOrdine)
                    Log.d("history2", "dentro ord vale $ord")
                    ords.add(ord)
                }
                Log.d("history2", "dentro ords vale $ords")
                mycallback.onCallback(ords)
            }
    }

    /*  ORDINI IN ARRIVO  */
    private fun getOrders(myCallbackOrders: MyCallbackConsegne) {
        val db = FirebaseFirestore.getInstance()
        val prodotti = arrayListOf<Prodotto>()
        val orders = arrayListOf<Consegna>()

        db.collection("toassignOrders").get()
            .addOnCompleteListener {
                for (d in it.result) {
                    var cliente = d.getString("cliente").toString()
                    var indirizzo = d.getString("indirizzo").toString()
                    var tipo_pagamento = d.getString("tipo").toString()

                    //distanza
                    val market = Location("")
                    market.latitude = 44.994154
                    market.longitude = 8.565942
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
                    val distanza = (market.distanceTo(cons_rider) / 1000).toDouble()
                    //distanza
                    val consegna = Consegna(cliente, prodotti, indirizzo, tipo_pagamento,-1, d.id, distanza, "non_selezionato")
                    orders.add(consegna)
                }
                myCallbackOrders.onCallback(orders)
            }
          }
    /* FINE ORDINI IN ARRIVO */
    fun convertLongToTime(time: Long?): String {
        if(time==null) return "err"
        val date = Date(time*1000)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)
        return format.format(date)
    }
    interface MyCallbackConsegne{
        fun onCallback(order: ArrayList<Consegna>)
    }
    interface MyCallback {
        fun onCallback(ords: ArrayList<Order>)
    }
    class Order(var id : String?,var cliente : String, var rider : String , var tipo : String, var arr : ArrayList<Prodotto>, var ratingQ : Int? = -1, var ratingV : Int? = -1, var ratingC : Int? = -1,var ratingRC : Int? = -1,var ratingRP : Int? = -1, var date : String, var tot : String , var richiamato : Int = 0, var risultatoOrdine : Int?) {
        override fun toString(): String {
            return "\n id : $id cliente : $cliente \n rider : $rider\n Tipo : $tipo \n ratingQ : $ratingQ \n ratingV : $ratingV \n ratingC : $ratingC \n date : $date \n arr : $arr \n risultatoOrdine : $risultatoOrdine "
        }
    }
}
