package it.uniupo.progetto

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.IOException

class ClientMappa : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val LOCATION_REQUEST_CODE = 101
    private var marker : Marker? = null
    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode
        )
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_mappa)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //geocoding da nome via a coordinate


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = false

        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_REQUEST_CODE)
        }

        var geocodeMatches: List<Address>? = null
        try{
            geocodeMatches = Geocoder(this).getFromLocationName("San Salvatore ,AL", 1)
        }catch (e: IOException){
            e.printStackTrace()
        }
        var zoomLevel = 11.0f

            for (mat in geocodeMatches!!) {
                val market = LatLng(mat.latitude, mat.longitude)
                mMap.addMarker(MarkerOptions()
                        .position(market).title("MiniMarket")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(market, zoomLevel))
                val circle = mMap.addCircle(CircleOptions()
                        .center(LatLng(mat.latitude, mat.longitude))
                        .radius(10000.0)
                        .strokeColor(Color.RED)
                        .fillColor(0x000C1FFFF)
                )
                val mypos = findViewById<ImageButton>(R.id.find)
                val search = findViewById<ImageButton>(R.id.search)
                val address = findViewById<EditText>(R.id.address)
                var cliente: LatLng
                zoomLevel=16.0f
                search.setOnClickListener {
                    //remove keyboard
                    val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    mgr.hideSoftInputFromWindow(address.getWindowToken(), 0)
                    try {
                        geocodeMatches = Geocoder(this).getFromLocationName("${address.text}", 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    for (mat in geocodeMatches!!) {
                        cliente = LatLng(mat.latitude, mat.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cliente, zoomLevel))
                                    if (cliente != null) {
                                     var x = Geocoder(this).getFromLocationName(address.text.toString(),1)
                                           for(mat in x){
                                               val cliente = LatLng(cliente.latitude, cliente.longitude)

                                               marker?.remove()
                                               marker = mMap.addMarker(MarkerOptions().position(cliente).title("Me"))
                                               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cliente, zoomLevel))
                                               var loc = Location(address.text.toString())
                                               loc.latitude = cliente.latitude
                                               loc.longitude = cliente.longitude
                                               //Log.d("indirizzo","${Location(address.text.toString())}")
                                               isInside(loc, circle)

                                           }
                                    }
                                }
                    }
                mypos.setOnClickListener{
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                            val cliente = LatLng(location.latitude, location.longitude)
                            marker?.remove()
                            marker = mMap.addMarker(MarkerOptions().position(cliente).title("Me"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cliente, zoomLevel))
                            isInside(location, circle)
                            }
                        }
                    }
                }
        }

    private fun setAddressFirebase(location: Location){
        var geocodeMatches: List<Address>? = null
        var indirizzo = ""
            try {
                geocodeMatches = Geocoder(this).getFromLocation(location!!.latitude, location.longitude, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (geocodeMatches != null) {
                indirizzo += geocodeMatches[0].getAddressLine(0) + " " + geocodeMatches[0].adminArea + " " + geocodeMatches[0].postalCode + " " + geocodeMatches[0].countryName
            }

            var currentUser = intent.getStringExtra("mail")!!
                Log.d("google", "se aggiorno ora vale $currentUser")
            val entry = hashMapOf<String, Any?>(
                    "address" to indirizzo,
            )
            var db: FirebaseFirestore = FirebaseFirestore.getInstance()
            db.collection("users").document(currentUser)
                    .set(entry, SetOptions.merge())
                    .addOnSuccessListener { documentReference ->
                        Log.d(
                                "indirizzo",
                                "indirizzo : $indirizzo "
                        )
                    }
                    .addOnFailureListener { e -> Log.w("---", "Error adding document", e) }
    }
    private fun isInside(location: Location?, circle: Circle) {
        var distance = FloatArray(2)
        if (location != null) {
            Location.distanceBetween(location!!.latitude, location.longitude, circle.center.latitude, circle.center.longitude, distance)
            if (distance[0] > circle.radius) {
                Toast.makeText(this, "Troppo distante", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(this, "Indirizzo valido", Toast.LENGTH_SHORT).show()
                val go = findViewById<Button>(R.id.go)
                go.visibility = View.VISIBLE
                go.setOnClickListener {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
            }
            setAddressFirebase(location)
        }
    }
}