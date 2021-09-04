package it.uniupo.progetto

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
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
import java.io.IOException
import java.lang.Double.parseDouble


class RiderPosition : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    lateinit var rider: String
    lateinit var email: String
    var zoomLevel = 16.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rider_position)


        email = FirebaseAuth.getInstance().currentUser!!.email.toString()
        getRiderFromClient(object : MyCallback {
            override fun onCallback(rid: String) {
                rider = rid
                val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this@RiderPosition)
                getRidersPositions(object : GestoreMappaRider.MyCallback {
                    override fun onCallback(positions: HashMap<String, LatLng>) {
                        positions.forEach {
                           Log.d("riders_pos", "${it.value} ")

                            if(rider==it.key) {
                                mMap.addMarker(MarkerOptions()
                                        .position(it.value).title(it.key)
                                        .icon(bitMapFromVector(R.drawable.rider))
                                )
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it.value, zoomLevel))

                            }
                        }

                    }
                })
            }
        }, email)

    }
    private fun getRidersPositions(mycallback: GestoreMappaRider.MyCallback){
        val db = FirebaseFirestore.getInstance()
        var positions  = hashMapOf<String, LatLng>()
        db.collection("riders").get()
                .addOnSuccessListener {
                    for(doc in it){
                        positions.put(doc.id,LatLng(doc.getDouble("lat")!!,doc.getDouble("lon")!!))
                    }
                    mycallback.onCallback(positions)
                }
    }
    private fun getRiderFromClient(mycallback: MyCallback, email: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("client-rider").document(email).collection("rider").get()
                .addOnSuccessListener { doc->
                   for(d in doc) mycallback.onCallback(d.id)
                }
    }
    interface MyCallback {
        fun onCallback(rider: String)
    }
    override fun onMapReady(p0: GoogleMap) {
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
//        TODO()
//        db.collection("delivery").document(rider).collection("client").document(email).collection("position").document("pos").get()
        db.collection("riders").document(rider).get()
                .addOnSuccessListener{ doc ->

                    if (doc.getDouble("lon")!=null && doc.getDouble("lat")!=null) {
                        Log.d("rider_pos", "rider -> ${doc.getDouble("lat")}, ${doc.getDouble("lon")}")
                        val riderPos = LatLng(doc.getDouble("lat")!!, doc.getDouble("lon")!!)
                        mMap.addMarker(MarkerOptions()
                                .position(riderPos)
                                .title("Rider")
                                .icon(bitMapFromVector(R.drawable.rider))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(riderPos, zoomLevel))
                    }

                }
    }
    private fun bitMapFromVector(vectorResID: Int):BitmapDescriptor {


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

}