package it.uniupo.progetto.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ncorti.slidetoact.SlideToActView
import it.uniupo.progetto.Consegna
import it.uniupo.progetto.R
import it.uniupo.progetto.RiderActivity
import it.uniupo.progetto.recyclerViewAdapter.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class Rider_ConsegneFragment(var address: String, var orderId: String, var userMail: String, var i: Int) : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    lateinit var viewConsegne: View
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        Log.d("inizio","i vale $i")
        if (i == 1) {
            viewConsegne = inflater.inflate(R.layout.activity_rider_delivery_info, container, false)
            //           class Rider_delivery_info : AppCompatActivity(), OnMapReadyCallback {
            //           override fun onCreate(savedInstanceState: Bundle?) {
            //             super.onCreate(savedInstanceState)
            //           viewConsegne = inflater.inflate(R.layout.activity_rider_delivery_info, container, false)
            val confermaPagamento = viewConsegne.findViewById<Button>(R.id.RiderConfermaPagamento) //bottone
            val rifiutaPagamento = viewConsegne.findViewById<Button>(R.id.RiderProblemiPagamento) //bottone
            val consegnaRider = viewConsegne.findViewById<SlideToActView>(R.id.ConsegnaRider) //slider

            confermaPagamento.visibility = View.VISIBLE
            rifiutaPagamento.visibility = View.VISIBLE
            val mapFragment = childFragmentManager
                    .findFragmentById(R.id.map_rider) as SupportMapFragment
            mapFragment.getMapAsync(this)

            confermaPagamento.setOnClickListener {
                confermaPagamentofun()
                Toast.makeText(viewConsegne.context, "Pagamento confermato", Toast.LENGTH_SHORT).show()
                consegnaRider.isLocked = false
                consegnaRider.visibility = View.VISIBLE
                Log.d("mattia", "Premuto confermaPagamento " + orderId)
            }
            rifiutaPagamento.setOnClickListener {
                rifiutaPagamentofun()
                Toast.makeText(viewConsegne.context, "Pagamento rifiutato", Toast.LENGTH_SHORT).show()
                consegnaRider.isLocked = false
                consegnaRider.visibility = View.VISIBLE
                Log.d("mattia", "Premuto rifiutaPagamento" + orderId)
            }

            consegnaRider!!.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    // devo prendere tutti i dati che sono presenti nella precedente activity e inserirli qui
                    Log.d("mattia", "Premuto terminaConsegna " + orderId)
                    terminaConsegnaFun(orderId)
                    var int = Intent(viewConsegne.context, RiderActivity::class.java)
                    int.putExtra("ordineAccettato",false)
                    startActivity(int)
                }
            }



    }
        else {
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
            getDisponibilita(object : myCallbackBoolean {
                override fun onCallback(ris: Boolean) {
                    delivery_switch.isChecked = ris
                }
            })

            delivery_switch.setOnClickListener {
                val status = delivery_switch.isChecked
                changeStatus(status)
            }

        }
            return viewConsegne

    }

    private fun rifiutaPagamentofun() {

        val db = FirebaseFirestore.getInstance()
        var rider = FirebaseAuth.getInstance().currentUser!!.email
        val det = hashMapOf<String, Any?>(
                "statoPagamento" to 0,
        )
//        Log.d("DELIVERY - ",orderId)
        db.collection("delivery").document(rider!!).collection("orders").get()
                .addOnCompleteListener {
                    for(d in it.result){
                        orderId = d.id
                        db.collection("delivery").document(rider!!).collection("orders").document(d.id).set(
                                det,
                                SetOptions.merge()
                        )
                    }
                }
    }

    override fun onMapReady(p0: GoogleMap) {
        var zoomLevel = 16.0f
        mMap = p0
        mMap.uiSettings.isMyLocationButtonEnabled = false
        var geocodeMatches: List<Address>? = null
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(viewConsegne.context)
        if (ActivityCompat.checkSelfPermission(viewConsegne.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        viewConsegne.context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap.isMyLocationEnabled = true;
        /*   val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
           fusedLocationClient.lastLocation
               .addOnSuccessListener { location: Location? ->
                   // Got last known location. In some rare situations this can be null.
                   if (location != null) {
                       val cliente = LatLng(location.latitude, location.longitude)
                       mMap.addMarker(MarkerOptions().position(cliente).title("Me"))
                       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cliente, zoomLevel))
                   }
               }*/
        try {
            Log.d("indirizzo","address vale $address")
            geocodeMatches = Geocoder(viewConsegne.context).getFromLocationName("circonvallazione ovest 35 valenza 15048", 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for (mat in geocodeMatches!!) {
            val riderPos = LatLng(mat.latitude, mat.longitude)
            mMap.addMarker(
                    MarkerOptions()
                            .position(riderPos)
                            .title("Destinazione")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(riderPos, zoomLevel))
        }

    }

    private fun confermaPagamentofun() {
        val db = FirebaseFirestore.getInstance()
        var rider = FirebaseAuth.getInstance().currentUser!!.email
        val det = hashMapOf<String, Any?>(
                "statoPagamento" to 1,
        )
//        Log.d("DELIVERY - ",orderId)
        db.collection("delivery").document(rider!!).collection("orders").get()
                .addOnCompleteListener {
                    for(d in it.result){
                        orderId = d.id
                        db.collection("delivery").document(rider!!).collection("orders").document(d.id).set(
                                det,
                                SetOptions.merge()
                        )
                    }
                }
    }

    private fun terminaConsegnaFun(orderId: String) {
        val db = FirebaseFirestore.getInstance()
        var rider = FirebaseAuth.getInstance().currentUser!!.email

        db.collection("delivery").document(rider!!).collection("orders").document(orderId).get()
                .addOnSuccessListener { doc ->

                    //la consegna può terminare solo se il pagamento è stato confermato( accettato/rifiutato)
                    if (doc.getLong("statoPagamento")!!.toInt() == -1) {
                        Log.d("mattia", "dentro if, non pagato " + doc.getLong("statoPagamento")!!.toInt())
                        Toast.makeText(viewConsegne.context, "Prima conferma il pagamento!!!!!", Toast.LENGTH_SHORT).show()

                    } else {
                        Log.d("mattia", "dentro else, pagato")
                        // termina consegna
                        val det = hashMapOf<String, Any?>(
                                "stato" to 1,
                        )
                        db.collection("delivery").document(rider!!).collection("orders").document(orderId).set(
                                det,
                                SetOptions.merge()
                        )
                        //rende di nuovo disponibile rider
                        val occ = hashMapOf<String, Any?>(
                                "disponibile" to false,
                        )
                        db.collection("riders").document(rider!!).set(occ, SetOptions.merge())
                        Log.d("mattia", "dopo azioni che funzionano ")

                        //salva in order_history
                        val client = doc.getString("client")
                        val stato = doc.getLong("stato")!!.toInt()
                        val statoPagamento = doc.getLong("statoPagamento")!!.toInt()
                        val tipoPagamento = doc.getString("tipo_pagamento")
                        //risultato ordine da fare successivamente
                        Log.d("mattia", "dopo di retrieve dati " + client + stato + statoPagamento + tipoPagamento)

                        val newOrderHistory = hashMapOf<String, Any?>(
                                "data" to Date(),
                                "mail" to client,
                                "rider" to rider!!,
                                "tipoPagamento" to tipoPagamento,
                                "statoPagamento" to statoPagamento,
                                "risultatoOrdine" to 1,
                                "ratingQ" to -1,
                                "ratingV" to -1,
                                "ratingC" to -1,

                                )
                        Log.d("mattia", "prima di aggiunta in order history: " + orderId + newOrderHistory)
                        db.collection("orders_history").document(orderId).set(newOrderHistory)
                        db.collection("delivery").document(rider!!).collection("orders").document(orderId).delete()
                        db.collection("toAssignOrders").document(rider!!).collection("orders").document(orderId).delete()

                        //cambiare  activity
                    }
                }
    }

    private fun getDisponibilita(mycallback: myCallbackBoolean) {
        val db = FirebaseFirestore.getInstance()
        val rider = FirebaseAuth.getInstance().currentUser!!.email.toString()
        db.collection("riders").document(rider).get()
                .addOnSuccessListener {
                    val occ = it.getBoolean("disponibile")
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
