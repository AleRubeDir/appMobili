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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
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
                Log.d(TAG,"2 type vale $type")
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
        Log.d(TAG, "onDestroy")
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
                            Log.d(TAG,"cliente vale $cliente")
                            //db.collection("orders").document(cliente).collection("order").addSnapshotListener { snap,e  ->
                            db.collection("toassignOrders").addSnapshotListener { snap,e  ->
                                if (snap != null) {
                                    Log.d(TAG,"dentro createNotification")
                                    val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                                    val mBuilder = NotificationCompat.Builder(applicationContext)
                                            .setContentTitle("Consegna in arrivo")
                                            .setContentText("Seleziona un rider per questa consegna ")
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
                val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
                getRiders(object : MyCallback{
                    override fun onCallback(ris: List<String>) {
                        for(rider in ris){
                            db.collection("delivery").document(rider).collection("orders").get()
                                    .addOnCompleteListener {
                                        for(ord in it.result!!){

                                            db.collection("delivery").document(rider).collection("orders").document(ord.id).addSnapshotListener{ d, e ->
                                                    Log.d(TAG,"------$rider----${ord.id}-----${d!!.getString("client").toString()}-----\n")
                                                  //  Log.d(TAG,"user = $user cliente =  ${d!!.getString("client").toString()} left = ${d.getBoolean("leftMM")}")
                                                  //  Log.d(TAG,"dentro --- ${user == d.getString("client").toString()} - ${d.getBoolean("leftMM")}")
                                                    if ((user == d.getString("client").toString()) && d.getBoolean("leftMM")!!) {
                                                   //     Log.d(TAG,"--- ${user == d.getString("client").toString()} - ${d.getBoolean("leftMM")}")
                                                        val intent = Intent(applicationContext, ChatActivity::class.java)
                                                        intent.putExtra("mail", rider)
                                                        val resultIntent = TaskStackBuilder.create(applicationContext).run {
                                                            // Add the intent, which inflates the back stack
                                                            addNextIntentWithParentStack(intent)
                                                            // Get the PendingIntent containing the entire back stack
                                                            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                                                        }
                                                        Log.d(TAG, "dentro createNotification")
                                                        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                                                        val mBuilder = NotificationCompat.Builder(applicationContext)
                                                                .setContentTitle("Rider partito")
                                                                .setContentText("Il rider ha appena lasciato il market, ora puoi chattare con lui")
                                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                                .setContentIntent(resultIntent)
                                                        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                                                        mBuilder.setAutoCancel(true)
                                                        mNotificationManager.notify(0, mBuilder.build())
                                                    }
                                                }
                                            }
                                        }
                        }
                    }
                })
            }
            "Rider" -> {
                val intent = Intent(applicationContext, RiderActivity::class.java)
                val resultIntent = TaskStackBuilder.create(this).run {
                    // Add the intent, which inflates the back stack
                    addNextIntentWithParentStack(intent)
                    // Get the PendingIntent containing the entire back stack
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }

                getRiders(object : MyCallback{
                    override fun onCallback(ris: List<String>) {
                        for(rider in ris){
                            db.collection("delivery").document(rider).collection("orders").addSnapshotListener{ snap, e ->
                                if (snap != null) {
                                    Log.d(TAG,"dentro createNotification")
                                    val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                                    val mBuilder = NotificationCompat.Builder(applicationContext)
                                            .setContentTitle("Consegna in arrivo")
                                            .setContentText("Scegli se accettarla o rifiutarla")
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
    private fun getRiders(myCallback: MyCallback) {
        val db = FirebaseFirestore.getInstance()
        var ris = mutableListOf<String>()
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
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private const val default_notification_channel_id = "default"
    }
}