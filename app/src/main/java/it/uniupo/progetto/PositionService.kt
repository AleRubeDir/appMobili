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
        return START_NOT_STICKY //servizio si stoppa quando si chiude l'app
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        startLocationUpdates()
             }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        stopLocationUpdates()
        super.onDestroy()
    }
        // Start location updates
        private fun startLocationUpdates() {
            Log.d("position","1 ")
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = LocationRequest.create()
            locationRequest.interval = 300000 //ms -> 5min
            locationRequest.fastestInterval = 300000 //setta max intervallo cpu
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //according to your app
            Log.d("position","Dentro getLocationUpdate")
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
            var name= ""
            var surname = ""
            db.collection("users").document(user).get()
                .addOnSuccessListener {
                     name = it.getString("name")!!
                     surname = it.getString("surname")!!
                }

                    locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)
                            Log.d("position","Dentro callback")
                            Log.d("position","locationresult vale ${locationResult.locations}")
                            if (!locationResult.locations.isNullOrEmpty()) {
                                val toSend = hashMapOf<String, Any?>(
                                        "nome" to name,
                                        "cognome" to surname,
                                        "lat" to  locationResult.lastLocation.latitude,
                                        "lon" to  locationResult.lastLocation.longitude,
                                        "disponibile" to true )
                                Log.d("position","aggiornamento posizione : ${locationResult.lastLocation}")
                                db.collection("riders").document(user).set(toSend, SetOptions.merge())
                            }
                        }
                    }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d("position","startLocationUpdates")
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            }

        }

        // Stop location updates
        private fun stopLocationUpdates() {
            Log.d("position","stopLocationUpdates")
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

    interface MyCallback{
        fun onCallback(ris: List<String>)
    }
}