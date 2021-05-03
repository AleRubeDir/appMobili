package it.uniupo.progetto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class ClientMappa : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val LOCATION_REQUEST_CODE = 101

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

        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_REQUEST_CODE)
        }
        mMap.isMyLocationEnabled = false

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        lateinit var mylocation : Location
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        mylocation = location
                        Toast.makeText(this, "Lat " + location.latitude + " and long: " + location.longitude, Toast.LENGTH_LONG).show()
                        Log.d("maps", "mylocation vale $mylocation")
                        // do something, save it perhaps?
                        var geocodeMatches: List<Address>? = null
                        try{
                            geocodeMatches = Geocoder(this).getFromLocationName("via Circonvallazione Ovest 35 Valenza", 1)
                        }catch (e: IOException){
                            e.printStackTrace()
                        }

                        if(geocodeMatches!=null){
                            for(mat in geocodeMatches) {
                                val market = LatLng(mat.latitude, mat.longitude)

                                mMap.addMarker(MarkerOptions().position(market).title("MiniMarket"))
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(market))
                                val circle = mMap.addCircle(CircleOptions()
                                        .center(LatLng(mat.latitude, mat.longitude))
                                        .radius(10000.0)
                                        .strokeColor(Color.RED)
                                        .fillColor(R.color.mapCircle))
                                if(isInside(mylocation.latitude,mylocation.longitude,circle)==1){
                                    //dentro
                                    startActivity(Intent(this, HomeActivity::class.java))
                                }else{
                                    //fuori
                                }

                                Log.d("maps", "mylocation vale $mylocation minimarket vale $market e circle vale ${circle}")


                            }
                        }
                    }
                }
    }
    private fun isInside(lat : Double, long : Double, circle : Circle):Int{
        var distance = FloatArray(2)
        Location.distanceBetween(lat, long,circle.center.latitude,circle.center.longitude,distance)
        if(distance[0]>circle.radius){
            Toast.makeText(this,"Oltre 10KM", Toast.LENGTH_SHORT).show()
            return 1
        }else{
            Toast.makeText(this,"Entro i 10KM", Toast.LENGTH_SHORT).show()
            return -1
        }
    }
}