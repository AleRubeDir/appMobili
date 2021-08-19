package it.uniupo.progetto

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
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
import java.lang.Double
import java.util.*
import kotlin.collections.HashMap

class GestoreMappaRider  : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    var zoomLevel = 11.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gestore_mappa_rider)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@GestoreMappaRider)
        getRidersPositions(object : MyCallback {
            override fun onCallback(positions: HashMap<String, LatLng>) {
                positions.forEach{
                    Log.d("riders_pos","${it.value} ")
                    val rider = it.value
                    Log.d("riders_pos","$rider")
                    mMap.addMarker(MarkerOptions()
                            .position(rider).title(it.key)
                            .icon(bitMapFromVector(R.drawable.rider))
                    )

                }



            }
        })
    }

//    private fun getRiderFromClient(mycallback: MyCallback, email: String) {
//        val db = FirebaseFirestore.getInstance()
//
//        db.collection("client-rider").document(email).collection("rider").get()
//                .addOnSuccessListener { doc->
//                    for(d in doc) mycallback.onCallback(d.id)
//                }
//    }
    private fun getRidersPositions(mycallback: MyCallback){
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
    interface MyCallback {
        fun onCallback(positions: HashMap<String, LatLng>)
    }


    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.uiSettings.isMyLocationButtonEnabled = false
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
}
