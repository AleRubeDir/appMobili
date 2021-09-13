package it.uniupo.progetto
import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper

import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.merge
import java.util.*

class PositionService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var TAG = "positionRider"
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG,"onStart")
        return START_STICKY
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        //tipo utente attivo
        getLozcationUpdates()
             }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }
    
//    private fun sendPosition() {
//        if(type=="Rider"){
//        var db = FirebaseFirestore.getInstance()
//        Log.d("positionRider","qui prima di loop")
//        Handler(Looper.getMainLooper()).postDelayed(
//        {
////            while (true){
//                Thread.sleep(10000)
//            Log.d("positionRider","inizio service ciclo ")
//            val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
//            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                fusedLocationClient.lastLocation
//                        .addOnSuccessListener { location: Location? ->
//                            // Got last known location. In some rare situations this can be null.
//                            if (location != null) {
//                                val toSend = hashMapOf<String, Any?>(
//
//                                        "lat" to location.latitude,
//                                        "lon" to location.longitude)
//
//                                db.collection("riders").document(user).set(toSend, SetOptions.merge())
//                            }
//                        }
//            }
//        },1000L)

        private fun getLozcationUpdates() {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = LocationRequest()
            locationRequest.interval = 50000
            locationRequest.fastestInterval = 50000
            locationRequest.smallestDisplacement = 170f //170 m = 0.1 mile
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //according to your app
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    //                    locationResult ?: return
                    if (locationResult!!.locations.isNotEmpty()) {
                        /*val location = locationResult.lastLocation
                        Log.e("location", location.toString())*/
                        val addresses: List<Address>?
                        val geoCoder = Geocoder(applicationContext, Locale.getDefault())
                        addresses = geoCoder.getFromLocation(
                                locationResult.lastLocation.latitude,
                                locationResult.lastLocation.longitude,
                                1
                        )
                        if (addresses != null && addresses.isNotEmpty()) {
                            val address: String = addresses[0].getAddressLine(0)
                            val city: String = addresses[0].locality
                            val state: String = addresses[0].adminArea
                            val country: String = addresses[0].countryName
                            val postalCode: String = addresses[0].postalCode
                            val knownName: String = addresses[0].featureName
                            Log.e("location", "$address $city $state $postalCode $country $knownName")
                        }
                    }
                }
            }
        }

        // Start location updates
        private fun startLocationUpdates() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null /* Looper */
                )
            }

        }

        // Stop location updates
        private fun stopLocationUpdates() {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

//        // Stop receiving location update when activity not visible/foreground
//        override fun onPause() {
//            super.onPause()
//            stopLocationUpdates()
//        }
//
//        // Start receiving location update when activity  visible/foreground
//        override fun onResume() {
//            super.onResume()
//            startLocationUpdates()
//        }





//    }


    private fun getClients(myCallback: MyCallback) {
        val db = FirebaseFirestore.getInstance()
        val ris = mutableListOf<String>()
        db.collection("orders").get()
                .addOnSuccessListener {
                    for (d in it){
                        ris.add(d.id)
                    }
                    myCallback.onCallback(ris)
                }
    }
    private fun getRiders(myCallback: MyCallback) {
        val db = FirebaseFirestore.getInstance()
        val ris = mutableListOf<String>()
        db.collection("riders").get()
                .addOnSuccessListener {
                    for (d in it){
                        ris.add(d.id)
                    }
                    myCallback.onCallback(ris)
                }
    }

    interface MyCallback{
        fun onCallback(ris: List<String>)
    }
}