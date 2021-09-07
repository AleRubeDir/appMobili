package it.uniupo.progetto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ncorti.slidetoact.SlideToActView
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Rider_delivery_info : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider_delivery_info)
        val ordine = intent.getBooleanExtra("ordineAccettato", false)

        if (ordine){
            onStartRiderActivity()
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map_rider) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val consegnaRider = findViewById<SlideToActView>(R.id.ConsegnaRider)

        consegnaRider!!.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                val orderId = intent.getStringExtra("orderId")!!
                Log.d("mattia", "Premuto terminaConsegna " + orderId)
                terminaConsegnaFun(orderId)
                //per tornare indietro con l'aztivity
                startActivity(Intent(applicationContext,RiderActivity::class.java))
            }
        }

        val confermaPagamento = findViewById<Button>(R.id.RiderConfermaPagamento)
        confermaPagamento.setOnClickListener{
            val orderId = intent.getStringExtra("orderId")!!
            confermaPagamentofun(orderId)
            Toast.makeText(applicationContext,"Pagamento confermato",Toast.LENGTH_SHORT).show()
            consegnaRider.isLocked = false
            consegnaRider.visibility = View.VISIBLE
            Log.d("mattia", "Premuto confermaPagamento " + orderId)
        }

        val errorePagamento = findViewById<Button>(R.id.RiderProblemiPagamento)
        errorePagamento.setOnClickListener{
            val orderId = intent.getStringExtra("orderId")!!
            errorePagamentofun(orderId)
            Toast.makeText(applicationContext,"Pagamento rifiutato",Toast.LENGTH_SHORT).show()
            consegnaRider.isLocked = false
            consegnaRider.visibility = View.VISIBLE
            Log.d("mattia", "Premuto errorePagamento " + orderId)
        }
    }

    private fun onStartRiderActivity(){
        val pagamentoRider = findViewById<Button>(R.id.RiderConfermaPagamento)
        pagamentoRider.visibility= View.VISIBLE
        val errorePagamentoRider = findViewById<Button>(R.id.RiderProblemiPagamento)
        errorePagamentoRider.visibility= View.VISIBLE
    }

    private fun errorePagamentofun(orderId: String) {
            val db = FirebaseFirestore.getInstance()
            val rider = FirebaseAuth.getInstance().currentUser!!.email
            val det = hashMapOf<String, Any?>(
                    "statoPagamento" to 0,
                    "stato" to 0,
            )
            db.collection("delivery").document(rider!!).collection("orders").document(orderId).set(
                    det,
                    SetOptions.merge()
            )
    }

    override fun onMapReady(p0: GoogleMap) {
        var zoomLevel = 16.0f
        mMap = p0
        mMap.uiSettings.isMyLocationButtonEnabled = false
        var geocodeMatches: List<Address>? = null
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap.isMyLocationEnabled = true;
        val address = intent.getStringExtra("address")!!
        try {
            geocodeMatches = Geocoder(applicationContext).getFromLocationName(address, 1)
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


    private fun confermaPagamentofun(orderId: String){
        val db = FirebaseFirestore.getInstance()
        var rider = FirebaseAuth.getInstance().currentUser!!.email
        val det = hashMapOf<String, Any?>(
                "statoPagamento" to 1,
        )
        db.collection("delivery").document(rider!!).collection("orders").document(orderId).set(
                det,
                SetOptions.merge()
        )
    }

    private fun terminaConsegnaFun(orderId: String){
        val db = FirebaseFirestore.getInstance()
        var rider = FirebaseAuth.getInstance().currentUser!!.email

        db.collection("delivery").document(rider!!).collection("orders").document(orderId).get()
                .addOnSuccessListener { doc ->

                    //la consegna può terminare solo se il pagamento è stato confermato( accettato/rifiutato)
                    if (doc.getLong("statoPagamento")!!.toInt() == -1) {
                        Log.d("mattia","dentro if, non pagato " + doc.getLong("statoPagamento")!!.toInt())
                        Toast.makeText(this, "Prima conferma il pagamento!!!!!", Toast.LENGTH_SHORT).show()

                    } else {
                        Log.d("mattia","dentro else, pagato")
                        // termina consegna
                        val det = hashMapOf<String, Any?>(
                                "stato" to 1,
                        )
                        db.collection("delivery").document(rider).collection("orders").document(orderId).set(
                                det,
                                SetOptions.merge()
                        )
                        //rende di nuovo disponibile rider
                        val occ = hashMapOf<String, Any?>(
                                "disponibile" to false,
                        )
                        db.collection("riders").document(rider).set(occ, SetOptions.merge())
                        Log.d("mattia", "dopo azioni che funzionano ")

                        //salva in order_history
                        val client = doc.getString("client")
                        val stato = doc.getLong("stato")!!.toInt()
                        val statoPagamento = doc.getLong("statoPagamento")!!.toInt()
                        val tipoPagamento = doc.getString("tipo_pagamento")
                        //risultato ordine da fare successivamente
                        Log.d("mattia", "dopo di retrieve dati "  + client + stato + statoPagamento + tipoPagamento)

                        val newOrderHistory = hashMapOf<String, Any?>(
                                "data" to  Date(),
                                "mail" to client,
                                "rider" to rider,
                                "tipoPagamento" to tipoPagamento,
                                "statoPagamento" to statoPagamento,
                                "risultatoOrdine" to 1,
                                "ratingQ" to -1,
                                "ratingV" to -1,
                                "ratingC" to -1,
                                "ratingRC" to -1,
                                "ratingRP" to -1,

                       )
                        Log.d("mattia", "prima di aggiunta in order history: " + orderId + newOrderHistory)
                        db.collection("orders_history").document(orderId).set(newOrderHistory)
                        db.collection("delivery").document(rider).collection("orders").document(orderId).delete()
                        db.collection("toAssignOrders").document(rider).collection("orders").document(orderId).delete()

                    }
                }
    }

}