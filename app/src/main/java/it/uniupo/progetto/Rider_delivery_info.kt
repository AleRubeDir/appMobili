package it.uniupo.progetto

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
<<<<<<< HEAD
import android.location.Geocoder
=======
>>>>>>> 16caefea8b77745f20c2fdf9d1fb1f94764b2d4f
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
<<<<<<< HEAD
=======
import android.widget.TextView
>>>>>>> 16caefea8b77745f20c2fdf9d1fb1f94764b2d4f
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
<<<<<<< HEAD
import java.io.IOException
=======
>>>>>>> 16caefea8b77745f20c2fdf9d1fb1f94764b2d4f
import java.text.SimpleDateFormat
import java.util.*


class Rider_delivery_info : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider_delivery_info)
        val ordine = intent.getBooleanExtra("ordineAccettato", false)
<<<<<<< HEAD
=======

>>>>>>> 16caefea8b77745f20c2fdf9d1fb1f94764b2d4f
        if (ordine){
            onStartRiderActivity()
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map_rider) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val consegnaRider = findViewById<SlideToActView>(R.id.ConsegnaRider)

        consegnaRider!!.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
//            devo prendere tutti i dati che sono presenti nella precedente activity e inserirli qui
                val orderId = intent.getStringExtra("orderId")!!
                Log.d("mattia", "Premuto terminaConsegna " + orderId)
                if(consegnaRider.isLocked)
                    Toast.makeText(applicationContext,"Conferma il pagamento prima di terminare la corsa",Toast.LENGTH_SHORT).show()
                terminaConsegnaFun(orderId)

            }
        }

        val confermaPagamento = findViewById<Button>(R.id.RiderConfermaPagamento)
        confermaPagamento.setOnClickListener{
            val orderId = intent.getStringExtra("orderId")!!
            confermaPagamentofun(orderId)
            consegnaRider.isLocked = false
            Log.d("mattia", "Premuto confermaPagamento " + orderId)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        var zoomLevel = 16.0f
        mMap = p0
        mMap.uiSettings.isMyLocationButtonEnabled = false
<<<<<<< HEAD

        var geocodeMatches: List<Address>? = null

=======
        var geocodeMatches: List<Address>? = null

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
>>>>>>> 16caefea8b77745f20c2fdf9d1fb1f94764b2d4f
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
<<<<<<< HEAD
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
/*        val db = FirebaseFirestore.getInstance()
=======
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        val cliente = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(cliente).title("Me"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cliente, zoomLevel))
                    }
                }
        val db = FirebaseFirestore.getInstance()
>>>>>>> 16caefea8b77745f20c2fdf9d1fb1f94764b2d4f
        val emailRider = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val orderId = intent.getStringExtra("orderId")
        db.collection("delivery").document(emailRider).collection("orders").document(orderId!!).get()
                .addOnSuccessListener{ doc ->

                    if (doc.getDouble("lon")!=null && doc.getDouble("lat")!=null) {
//                        Log.d("rider_pos", "rider -> ${doc.getDouble("lat")}, ${doc.getDouble("lon")}")
                        val riderPos = LatLng(doc.getDouble("lat")!!, doc.getDouble("lon")!!)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(riderPos)
                                .title("Destinazione")
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(riderPos, zoomLevel))
                    }

<<<<<<< HEAD
                }*/
=======
                }
    }

    private fun bitMapFromVector(vectorResID: Int): BitmapDescriptor {

        val background = ContextCompat.getDrawable(applicationContext, R.drawable.ic_pin)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable= ContextCompat.getDrawable(applicationContext!!, vectorResID)

        val left = (background.intrinsicWidth - vectorDrawable!!.intrinsicWidth) / 2
        val top = (background.intrinsicHeight - vectorDrawable!!.intrinsicHeight) / 3
        vectorDrawable!!.setBounds(
            left,
            top,
            left + vectorDrawable.intrinsicWidth,
            top + vectorDrawable.intrinsicHeight
        )

        val bitmap= Bitmap.createBitmap(
            background!!.intrinsicWidth,
            background.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas= Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable!!.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
>>>>>>> 16caefea8b77745f20c2fdf9d1fb1f94764b2d4f
    }

    private fun onStartRiderActivity(){
        val consegnaRider = findViewById<SlideToActView>(R.id.ConsegnaRider)
        consegnaRider.visibility= View.VISIBLE

        val pagamentoRider = findViewById<Button>(R.id.RiderConfermaPagamento)
        pagamentoRider.visibility= View.VISIBLE
    }

    private fun confermaPagamentofun(orderId: String){
        val db = FirebaseFirestore.getInstance()
        var rider = FirebaseAuth.getInstance().currentUser!!.email
        val det = hashMapOf<String, Any?>(
                "statoPagamento" to 1,
        )
//        Log.d("DELIVERY - ",orderId)
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
<<<<<<< HEAD
                                "stato" to "terminato",
=======
                                //stato terminato
                                "stato" to 1,
>>>>>>> 16caefea8b77745f20c2fdf9d1fb1f94764b2d4f
                        )
                        db.collection("delivery").document(rider!!).collection("orders").document(orderId).set(
                                det,
                                SetOptions.merge()
                        )
                        //rende di nuovo disponibile rider
                        val occ = hashMapOf<String, Any?>(
                                "occupato" to false,
                        )
                        db.collection("riders").document(rider!!).set(occ, SetOptions.merge())
                        Log.d("mattia", "dopo azioni che funzionano ")

                        //salva in order_history
                        val client = doc.getString("client")
<<<<<<< HEAD
                        val stato = doc.getString("stato")
=======
                        val stato = doc.getLong("stato")!!.toInt()
>>>>>>> 16caefea8b77745f20c2fdf9d1fb1f94764b2d4f
                        val statoPagamento = doc.getLong("statoPagamento")!!.toInt()
                        val tipoPagamento = doc.getString("tipo_pagamento")
                        //risultato ordine da fare successivamente

                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val currentDate = sdf.format(Date())
                        Log.d("mattia", "dopo di retrieve dati "  + client + stato + statoPagamento + tipoPagamento)

                        val newOrderHistory = hashMapOf<String, Any?>(
                                "data" to  currentDate,
                                "mail" to client,
                                "rider" to rider!!,
                                "tipo" to tipoPagamento,
                                "risultatoOrdine" to statoPagamento
                       )
                        Log.d("mattia", "prima di aggiunta in order history: " + orderId + newOrderHistory)
                        db.collection("orders_history").document(orderId).set(newOrderHistory)

                        //cambiare  activity
                    }
                }
    }


}