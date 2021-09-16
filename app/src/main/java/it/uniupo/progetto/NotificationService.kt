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
import androidx.core.app.NotificationManagerCompat
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

                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        val name = getString(R.string.notificheGestore)
                                        val descriptionText =  getString(R.string.notificheGestore)
                                        val importance = NotificationManager.IMPORTANCE_DEFAULT
                                        val CHANNEL_ID = getString(R.string.notificheGestore)
                                        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                                            description = descriptionText
                                        }
                                        // Register the channel with the system
                                        val notificationManager: NotificationManager =
                                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                        notificationManager.createNotificationChannel(channel)

                                        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle("Consegna in arrivo")
                                                .setContentText("Seleziona un rider per questa consegna ")
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                .setContentIntent(resultIntent)

                                        with(NotificationManagerCompat.from(applicationContext)) {
                                            notify(1, builder.build())
                                        }
                                    }else {
                                        Log.d("NOTIFICA","<oreo")
                                        val mBuilder = Notification.Builder(applicationContext)
                                                .setContentTitle("Consegna in arrivo")
                                                .setContentText("Seleziona un rider per questa consegna ")
                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                                        mBuilder.setAutoCancel(true)
                                        mNotificationManager.notify(0, mBuilder.build())
                                    }
                                }
                                e?.printStackTrace()
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
                                        for(ord in it.result!!) {
                                            db.collection("delivery").document(rider).collection("orders").document(ord.id).addSnapshotListener { d, e ->
                                                if (d != null) {
                                                    if ((user == d.getString("client").toString()) && d.getBoolean("leftMM") == true) {
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
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            val name = getString(R.string.notificheCliente)
                                                            val descriptionText = getString(R.string.notificheCliente)
                                                            val importance = NotificationManager.IMPORTANCE_DEFAULT
                                                            val CHANNEL_ID = getString(R.string.notificheCliente)
                                                            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                                                                description = descriptionText
                                                            }
                                                            // Register the channel with the system
                                                            val notificationManager: NotificationManager =
                                                                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                                            notificationManager.createNotificationChannel(channel)

                                                            val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                                                                    .setSmallIcon(R.mipmap.ic_launcher)
                                                                    .setContentTitle("Rider partito")
                                                                    .setContentText("Il rider ha appena lasciato il market, ora puoi chattare con lui")
                                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                                    .setContentIntent(resultIntent)
                                                            with(NotificationManagerCompat.from(applicationContext)) {
                                                                notify(2, builder.build())
                                                            }
                                                        } else {
                                                            Log.d("NOTIFICA", "<oreo")
                                                            val mBuilder = Notification.Builder(applicationContext)
                                                                    .setContentTitle("Rider partito")
                                                                    .setContentText("Il rider ha appena lasciato il market, ora puoi chattare con lui")
                                                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                                                            mBuilder.setAutoCancel(true)
                                                            mNotificationManager.notify(0, mBuilder.build())
                                                        }

                                                    }
                                                } else e?.printStackTrace()
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
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        val name = getString(R.string.notificheRider)
                                        val descriptionText = getString(R.string.notificheRider)
                                        val importance = NotificationManager.IMPORTANCE_DEFAULT
                                        val CHANNEL_ID = getString(R.string.notificheRider)
                                        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                                            description = descriptionText
                                        }
                                        // Register the channel with the system
                                        val notificationManager: NotificationManager =
                                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                        notificationManager.createNotificationChannel(channel)

                                        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle("Ordine ASSEGNATO")
                                                .setContentText("Ti è stato assegnato un nuovo ordine.")
//                                                .setStyle(NotificationCompat.BigTextStyle()
//                                                        .bigText("Much longer text that cannot fit one line..."))
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                .setContentIntent(resultIntent)

                                        with(NotificationManagerCompat.from(applicationContext)) {
                                            // notificationId is a unique int for each notification that you must define
                                            notify(3, builder.build())
                                        }
                                    }else {
                                        Log.d("NOTIFICA","<oreo")
                                        val mBuilder = Notification.Builder(applicationContext)
                                                .setContentTitle("Ordine ASSEGNATO")
                                                .setContentText("Ti è stato assegnato un nuovo ordine.")
                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                                        mBuilder.setAutoCancel(true)
                                        mNotificationManager.notify(0, mBuilder.build())
                                    }


                                }
                                else e?.printStackTrace()
                            }
                        }
                    }
                })
            }
        }
    }

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