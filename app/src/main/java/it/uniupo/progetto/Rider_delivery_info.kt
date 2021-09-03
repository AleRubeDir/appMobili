package it.uniupo.progetto

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Rider_delivery_info : AppCompatActivity() {
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider_delivery_info)
        val ordine = intent.getBooleanExtra("ordineAccettato",false)
        if (ordine){
            onStartRiderActivity()
        }
        val terminaConsegna = findViewById<Button>(R.id.ConsegnaRider)
        terminaConsegna.setOnClickListener{
//            devo prendere tutti i dati che sono presenti nella precedente activity e inserirli qui
            val orderId = intent.getStringExtra("orderId")!!
            Log.d("mattia","Premuto terminaConsegna"+orderId)

            terminaConsegnaFun(orderId)
        }

        val confermaPagamento = findViewById<Button>(R.id.RiderConfermaPagamento)
        confermaPagamento.setOnClickListener{
//            devo prendere tutti i dati che sono presenti nella precedente activity e inserirli qui
            val orderId = intent.getStringExtra("orderId")
            Log.d("mattia","Premuto terminaConsegna"+orderId)
        }
    }

    fun onMapReady(p0: GoogleMap) {
        var zoomLevel = 16.0f
        mMap = p0
        mMap.uiSettings.isMyLocationButtonEnabled = false
        var geocodeMatches: List<Address>? = null

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
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
        val emailRider = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val orderId = intent.getStringExtra("orderId")
        db.collection("delivery").document(emailRider).collection(orderId!!).document("dett").get()
                .addOnSuccessListener{ doc ->

                    if (doc.getDouble("lon")!=null && doc.getDouble("lat")!=null) {
//                        Log.d("rider_pos", "rider -> ${doc.getDouble("lat")}, ${doc.getDouble("lon")}")
                        val riderPos = LatLng(doc.getDouble("lat")!!, doc.getDouble("lon")!!)
                        mMap.addMarker(MarkerOptions()
                                .position(riderPos)
                                .title("Destinazione")
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(riderPos, zoomLevel))
                    }

                }
    }

    private fun bitMapFromVector(vectorResID: Int): BitmapDescriptor {

        val background = ContextCompat.getDrawable(applicationContext, R.drawable.ic_pin)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable= ContextCompat.getDrawable(applicationContext!!, vectorResID)

        val left = (background.intrinsicWidth - vectorDrawable!!.intrinsicWidth) / 2
        val top = (background.intrinsicHeight - vectorDrawable!!.intrinsicHeight) / 3
        vectorDrawable!!.setBounds(left, top, left + vectorDrawable.intrinsicWidth, top + vectorDrawable.intrinsicHeight)

        val bitmap= Bitmap.createBitmap(background!!.intrinsicWidth, background.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable!!.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun onStartRiderActivity(){
        val consegnaRider = findViewById<Button>(R.id.ConsegnaRider)
        consegnaRider.visibility= View.VISIBLE

        val pagamentoRider = findViewById<Button>(R.id.RiderConfermaPagamento)
        pagamentoRider.visibility= View.VISIBLE
    }

    private fun terminaConsegnaFun(orderId: String){
        val db = FirebaseFirestore.getInstance()
        var rider = FirebaseAuth.getInstance().currentUser!!.email
        val det = hashMapOf<String, Any?>(
                "stato" to "terminato",

        )
//        Log.d("DELIVERY - ",orderId)
        db.collection("delivery").document(rider!!).collection("orders").document(orderId).set(det, SetOptions.merge())

        val det1 = hashMapOf<String, Any?>(
                "occupato" to false,
        )
//        Log.d("DELIVERY - ",orderId)
        db.collection("riders").document(rider!!).set(det1, SetOptions.merge())




    }
}