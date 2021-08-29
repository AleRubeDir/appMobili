package it.uniupo.progetto
import android.app.*
import it.uniupo.progetto.R
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.IBinder

import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

class NotificationService : Service() {
    var TAG = "notifications"
    lateinit var notificationManager : NotificationManager
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG,"onStart")
        return START_STICKY
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        //tipo utente attivo
        val usr = FirebaseAuth.getInstance().currentUser!!.email.toString()
        getUserType(usr, object : LoginActivity.FirestoreCallback {
            override fun onCallback(type: String) {
            createNotification("type")
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
        when (tipo) {
            "Gestore" -> {
                val intent = Intent(applicationContext, GestoreActivity::class.java)
                intent.putExtra("from_notification","true")
                val resultIntent = TaskStackBuilder.create(this).run {
                    // Add the intent, which inflates the back stack
                    addNextIntentWithParentStack(intent)
                    // Get the PendingIntent containing the entire back stack
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }


                getClients(object : MyCallback {
                    override fun onCallback(ris: List<String>) {
                        for(cliente in ris) {
                            db.collection("orders").document(cliente).collection("order").addSnapshotListener { snap,e  ->
                                if (snap != null) {
                                    Log.d(TAG,"dentro createNotification")
                                    val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                                    val mBuilder = NotificationCompat.Builder(applicationContext)
                                            .setContentTitle("Consegna in arrivo")
                                            .setContentText("Seleziona il rider per questa consegna")
                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                            .setContentIntent(resultIntent)
                                    mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                                    mBuilder.setAutoCancel(true)
                                    mNotificationManager.notify(0, mBuilder.build())
                                }
                            }
                        }
                    }
                })

            }
            "Cliente" -> {
                TODO()
            }
            "Rider" -> {
                TODO()
            }
        }
    }

    private fun getClients(myCallback: MyCallback) {
        val db = FirebaseFirestore.getInstance()
        var ris = mutableListOf<String>()
        db.collection("orders").get()
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
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private const val default_notification_channel_id = "default"
    }
}