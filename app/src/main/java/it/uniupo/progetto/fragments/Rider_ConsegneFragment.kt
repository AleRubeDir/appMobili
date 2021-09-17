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
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
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
import it.uniupo.progetto.*
import it.uniupo.progetto.recyclerViewAdapter.*
import java.io.IOException
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class Rider_ConsegneFragment() : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    lateinit var viewConsegne: View
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        Log.d("inizio", RiderActivity.ind + RiderActivity.ordId + RiderActivity.userMail + RiderActivity.flag_consegna )
        if (RiderActivity.flag_consegna == 1) {

            viewConsegne = inflater.inflate(R.layout.activity_rider_delivery_info, container, false)
            val confermaPagamento = viewConsegne.findViewById<Button>(R.id.RiderConfermaPagamento) //bottone
            val rifiutaPagamento = viewConsegne.findViewById<Button>(R.id.RiderProblemiPagamento) //bottone
            val partenzaMM = viewConsegne.findViewById<Button>(R.id.leftMMButton) //bottone
            val consegnaRider = viewConsegne.findViewById<SlideToActView>(R.id.ConsegnaRider) //slider

            partenzaMM.visibility = RiderActivity.partenzaMMVisibility
            confermaPagamento.visibility = RiderActivity.confermaPagamentovisibility
            rifiutaPagamento.visibility = RiderActivity.rifiutaPagamentovisibility
            val mapFragment = childFragmentManager
                    .findFragmentById(R.id.map_rider) as SupportMapFragment
            mapFragment.getMapAsync(this)

            partenzaMM.setOnClickListener{
                RiderActivity.confermaPagamentovisibility = View.VISIBLE
                RiderActivity.rifiutaPagamentovisibility = View.VISIBLE
                RiderActivity.partenzaMMVisibility = View.INVISIBLE
                partenzaMM.visibility = View.INVISIBLE
                confermaPagamento.visibility = RiderActivity.confermaPagamentovisibility
                rifiutaPagamento.visibility = RiderActivity.rifiutaPagamentovisibility
                setLeftMMDB()
//                manda notifica
            }
            confermaPagamento.setOnClickListener {
                confermaPagamentofun()
                Toast.makeText(viewConsegne.context, "Pagamento confermato", Toast.LENGTH_SHORT).show()
                consegnaRider.isLocked = false
                consegnaRider.visibility = View.VISIBLE
                Log.d("mattia", "Premuto confermaPagamento " + RiderActivity.ordId!!)
            }
            rifiutaPagamento.setOnClickListener {
                rifiutaPagamentofun()
                Toast.makeText(viewConsegne.context, "Pagamento rifiutato", Toast.LENGTH_SHORT).show()
                consegnaRider.isLocked = false
                consegnaRider.visibility = View.VISIBLE
                Log.d("mattia", "Premuto rifiutaPagamento" + RiderActivity.ordId!!)
            }

            consegnaRider!!.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    // devo prendere tutti i dati che sono presenti nella precedente activity e inserirli qui
                    Log.d("mattia", "Premuto terminaConsegna " + RiderActivity.ordId!!)
                    terminaConsegnaFun()
                    val int = Intent(viewConsegne.context, RiderActivity::class.java)
                    int.putExtra("deletePrefOrd",true)
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

    private fun setLeftMMDB() {
        val db = FirebaseFirestore.getInstance()
        val rider = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val entry = hashMapOf<String,Any>(
                "leftMM" to true
        )
        Log.d("inizio","ordId per leftmm vale ${RiderActivity.ordId}")
        db.collection("delivery").document(rider).collection("orders").document(RiderActivity.ordId.toString()).set(entry, SetOptions.merge())

    }

    private fun rifiutaPagamentofun() {

        val db = FirebaseFirestore.getInstance()
        var rider = FirebaseAuth.getInstance().currentUser!!.email
        val det = hashMapOf<String, Any?>(
                "risultatoOrdine" to 0,
                "statoPagamento" to 0
        )
//        Log.d("DELIVERY - ",orderId)
        db.collection("delivery").document(rider!!).collection("orders").get()
                .addOnCompleteListener {
                    for(d in it.result){
                        db.collection("delivery").document(rider).collection("orders").document(d.id).set(
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
        if (ActivityCompat.checkSelfPermission(viewConsegne.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        viewConsegne.context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap.isMyLocationEnabled = true;
        try {
            geocodeMatches = Geocoder(viewConsegne.context).getFromLocationName(RiderActivity.ind, 1)
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
                "risultatoOrdine" to 1,
                "statoPagamento" to 1,
        )
//        Log.d("DELIVERY - ",orderId)
        db.collection("delivery").document(rider!!).collection("orders").get()
                .addOnCompleteListener {
                    for(d in it.result){
                        db.collection("delivery").document(rider).collection("orders").document(d.id).set(
                                det,
                                SetOptions.merge()
                        )
                    }
                }
    }

    fun terminaConsegnaFun() {
        val db = FirebaseFirestore.getInstance()
        var rider = FirebaseAuth.getInstance().currentUser!!.email

        db.collection("delivery").document(rider!!).collection("orders").document(RiderActivity.ordId!!).get()
                .addOnSuccessListener { doc ->
                    if (doc.getLong("risultatoOrdine")!!.toInt()==0){
                        db.collection("orders").document(doc.getString("mail").toString()).collection("order").document(RiderActivity.ordId!!).collection("products").get()
                                .addOnSuccessListener {
                                    for(prod in it)
                                    //CartListFragment().diminuisciQtaDB(Prodotto(p.getLong("id")!!.toInt(),"","","","",p.getLong("qta")!!.toInt()))
                                    db.collection("products").document(prod.id).get()
                                            .addOnSuccessListener { document->
                                                val p = Prodotto(prod.getLong("id")!!.toInt(),"","","","",prod.getLong("qta")!!.toInt())
                                                            var newqta = document.getLong("qta")!!.toInt() + p.qta
                                                            val entry = hashMapOf<String, Any?>(
                                                                    "qta" to newqta,
                                                            )
                                                            db.collection("products").document(p.id.toString()).set(entry, SetOptions.merge())
                                            }

                                }

                    }
                    //la consegna può terminare solo se il pagamento è stato confermato( accettato/rifiutato)
                    if (doc.getLong("statoPagamento")!!.toInt() == -1) {
                        Toast.makeText(viewConsegne.context, "Prima conferma il pagamento!!!!!", Toast.LENGTH_SHORT).show()
                    } else {
                        val det = hashMapOf<String, Any?>(
                                "statoPagamento" to 1,
                        )
                        db.collection("delivery").document(rider).collection("orders").document(RiderActivity.ordId!!).set(
                                det,
                                SetOptions.merge()
                        )
                        val occ = hashMapOf<String, Any?>(
                                "disponibile" to true,
                        )
                        db.collection("riders").document(rider).set(occ, SetOptions.merge())
                        Log.d("mattia", "dopo azioni che funzionano ")

                        //salva in order_history
                        val client = doc.getString("client")
                        val stato = doc.getLong("stato")!!.toInt()
                        val statoPagamento = doc.getLong("statoPagamento")!!.toInt()
                        val tipoPagamento = doc.getString("tipo_pagamento")
                        val risultatoOrdine = doc.getLong("risultatoOrdine")!!.toInt()
                        //risultato ordine da fare successivamente
                        Log.d("mattia", "dopo di retrieve dati " + client + stato + statoPagamento + tipoPagamento)

                        val newOrderHistory = hashMapOf<String, Any?>(
                                "data" to Date(),
                                "mail" to client,
                                "rider" to rider,
                                "tipoPagamento" to tipoPagamento,
                                "statoPagamento" to statoPagamento,
                                "risultatoOrdine" to risultatoOrdine,
                                "ratingQ" to -1,
                                "ratingV" to -1,
                                "ratingC" to -1,
                                "ratingRC" to -1,
                                "ratingRP" to -1,

                                )
                        db.collection("delivery").document(rider).collection("orders").document(RiderActivity.ordId!!).delete()
                        db.collection("delivery").document(rider).delete()
                        db.collection("chats").document(rider).collection("contacts").document(RiderActivity.userMail!!).delete()
                        db.collection("chats").document(RiderActivity.userMail!!).collection("contacts").document(rider).collection("messages").get()
                                .addOnCompleteListener {
                                    for (d in it.result) d.reference.delete()
                                }
                        db.collection("client-rider").document(client!!).collection("rider").document(rider).delete()
                        db.collection("client-rider").document(client).delete()
                        db.collection("orders").document(client).collection("order").document(RiderActivity.ordId!!).collection("details").document("dett").delete()
                        db.collection("orders").document(client).collection("order").document(RiderActivity.ordId!!).collection("products").get()
                                .addOnSuccessListener {
                                    for (d in it) {
                                        db.collection("orders").document(client).collection("order").document(RiderActivity.ordId!!).collection("products").document(d.id).delete()
                                    }
                                }
                        db.collection("orders").document(client).collection("order").document(RiderActivity.ordId!!).collection("rider").document("r").delete()
                        db.collection("orders").document(client).collection("order").document(RiderActivity.ordId!!).delete()
                        db.collection("orders").document(client).delete().addOnSuccessListener {
                            db.collection("orders_history").document(RiderActivity.ordId!!).set(newOrderHistory).addOnSuccessListener {
                                db.collection("assignedOrders").document(RiderActivity.ordId!!).delete()
                                var int = Intent(viewConsegne.context, RiderActivity::class.java)
                                int.putExtra("ordineAccettato", false)
                                RiderActivity.ind = ""
                                RiderActivity.ordId = ""
                                RiderActivity.userMail = ""
                                RiderActivity.flag_consegna = 0
                                startActivity(int)
                            }
                        }
                    }
                }
    }

    private fun getDisponibilita(mycallback: myCallbackBoolean) {
        val db = FirebaseFirestore.getInstance()
        val rider = FirebaseAuth.getInstance().currentUser!!.email.toString()
        db.collection("riders").document(rider).get()
                .addOnSuccessListener {
                    val occ = it.getBoolean("disponibile")
                    if(occ!=null)
                     mycallback.onCallback(occ)
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
                        val distanza = order.getDouble("distanza")
                        val tipo_pagamento = order.getString("tipo_pagamento").toString()
                        var posizione : String
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



}

