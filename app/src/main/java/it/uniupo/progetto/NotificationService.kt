package it.uniupo.progetto
import it.uniupo.progetto.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder

import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class NotificationService : Service() {
    var TAG = "notifications"
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
        //tipo utente attivo

        val usr = FirebaseAuth.getInstance().currentUser!!.email
        getUserType(usr, object : LoginActivity.FirestoreCallback {
            override fun onCallback(type: String) {
                createNotification(type)
            }
        })

    }
    private fun getUserType(user: String?, fc: LoginActivity.FirestoreCallback){
        val db = FirebaseFirestore.getInstance()
        var t = "null"
        db.collection("users").document(user!!)
            .get()
            .addOnSuccessListener { result ->
                if(!result.getString("type").isNullOrBlank())
                    t = result.getString("type")!!
                fc.onCallback(t)
            }
    }
    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
    }

    private fun createNotification(tipo : String) {


        val db = FirebaseFirestore.getInstance()

        if(tipo=="Gestore") {

            db.collection("orders").addSnapshotListener { e, snap ->
                if (snap != null) {
                    val mNotificationManager =
                        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    val mBuilder = NotificationCompat.Builder(applicationContext)
                        .setContentTitle("Consegna in arrivo")
                        .setContentText("Seleziona il rider per questa consegna")
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    mBuilder.setAutoCancel(true)
                    mNotificationManager.notify(0, mBuilder.build())
                }
            }
        }else if(tipo=="Cliente"){
            TODO()
        }else if(tipo=="Rider"){
            TODO()
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private const val default_notification_channel_id = "default"
    }
}