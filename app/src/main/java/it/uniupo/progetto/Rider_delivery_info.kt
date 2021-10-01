package it.uniupo.progetto

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var sp: SharedPreferences
    private val LOCATION_REQUEST_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider_delivery_info)
        val ordine = intent.getBooleanExtra("ordineAccettato", false)
        Log.d("sharedPref", " xxxx")
        sp = applicationContext.getSharedPreferences("ordineAccettato", 0)

        if (ordine || sp.getBoolean("ordineAccettato", false)) {
            onStartRiderActivity()
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map_rider) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        val consegnaRider = findViewById<SlideToActView>(R.id.ConsegnaRider)

        val confermaPagamento = findViewById<Button>(R.id.RiderConfermaPagamento)
        confermaPagamento.setOnClickListener {
            val orderId = intent.getStringExtra("orderId")!!
            confermaPagamentofun(orderId)
            Toast.makeText(applicationContext, "Pagamento confermato", Toast.LENGTH_SHORT).show()
            consegnaRider.isLocked = false
            consegnaRider.visibility = View.VISIBLE
            Log.d("mattia", "Premuto confermaPagamento " + orderId)
        }

        val errorePagamento = findViewById<Button>(R.id.RiderProblemiPagamento)
        errorePagamento.setOnClickListener {
            val orderId = intent.getStringExtra("orderId")!!
            errorePagamentofun(orderId)
            Toast.makeText(applicationContext, "Pagamento rifiutato", Toast.LENGTH_SHORT).show()
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
    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode
        )
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Unable to show location - permission required",
                            Toast.LENGTH_LONG).show()
                } else {
                    val mapFragment = supportFragmentManager
                            .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        Log.d("posizioneRider","dentro OMR, fine = ${ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)} coarse = ${ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)}")
        var zoomLevel = 16.0f
        mMap = p0
        mMap.uiSettings.isMyLocationButtonEnabled = false
        var geocodeMatches: List<Address>? = null

        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_REQUEST_CODE)
        }

        val address = intent.getStringExtra("address")!!
        Log.d("posizioneRider","address vale $address")
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
}