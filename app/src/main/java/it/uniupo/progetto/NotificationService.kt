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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.util.*

class NotificationService : Service() {
    var TAG = "notifications"
    lateinit var notificationManager: NotificationManager
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Log.d(TAG, "onStart")
        return START_STICKY
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        //tipo utente attivo
        val usr = FirebaseAuth.getInstance().currentUser!!.email.toString()
        getUserType(usr, object : LoginActivity.FirestoreCallback {
            override fun onCallback(type: String) {
                Log.d(TAG, "2 type vale $type")
                createNotification(type)
            }
        })

    }

    private fun getUserType(user: String?, fc: LoginActivity.FirestoreCallback) {
        val db = FirebaseFirestore.getInstance()
        var t = "null"
        db.collection("users").document(user!!)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.getString("type").isNullOrBlank())
                        t = result.getString("type")!!
                    fc.onCallback(t)
                }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        notificationManager.cancelAll()
        super.onDestroy()
    }

    private fun clienteNotifications() {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
        getRiders(object : MyCallback {
            override fun onCallback(ris: List<String>) {
                for (rider in ris) {
                    db.collection("delivery").document(rider).collection("orders").get()
                            .addOnCompleteListener {
                                for (ord in it.result!!) {
                                    db.collection("delivery").document(rider).collection("orders").document(ord.id).addSnapshotListener { d, e ->
                                        if (d != null) {
                                            if ((user == d.getString("client").toString()) && d.getBoolean("leftMM") == true && d.getBoolean("sendNot")==true) {
                                                val updateSendNot = hashMapOf<String,Boolean>(
                                                        "sendNot" to false
                                                )
                                                db.collection("delivery").document(rider).collection("orders").document(ord.id).set(updateSendNot, SetOptions.merge())
                                                val intent = Intent(applicationContext, ChatActivity::class.java)
                                                intent.putExtra("mail", rider)
                                                val resultIntent = TaskStackBuilder.create(applicationContext).run {
                                                    // Add the intent, which inflates the back stack
                                                    addNextIntentWithParentStack(intent)
                                                    // Get the PendingIntent containing the entire back stack
                                                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                                                }
                                                Log.d(TAG, "dentro createNotification")
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    val name = getString(R.string.notificheCliente)
                                                    val descriptionText = getString(R.string.notificheCliente)
                                                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                                                    val CHANNEL_ID = getString(R.string.notificheCliente)
                                                    val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                                                        description = descriptionText
                                                    }
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
                                                            .setContentIntent(resultIntent)
                                                    mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                                                    mBuilder.setAutoCancel(true)
                                                    notificationManager.notify(0, mBuilder.build())
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

    private fun gestoreNotifications() {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val intent = Intent(applicationContext, GestoreActivity::class.java)
        intent.putExtra("from_notification", "true")
        val resultIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        getClients(object : MyCallback {
            override fun onCallback(ris: List<String>) {
                var check = false
                for (cliente in ris) {
                    Log.d(TAG, "cliente vale $cliente")
                    //db.collection("orders").document(cliente).collection("order").addSnapshotListener { snap,e  ->
                    db.collection("toassignOrders").addSnapshotListener { snap, e ->
                        if (snap != null && !snap.isEmpty) {
                            for(d in snap.documents){
                                if(d.id.isNotEmpty()) check = true
                            }
                            if(check){
                            Log.d(TAG, "dentro createNotification")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val name = getString(R.string.notificheGestore)
                                val descriptionText = getString(R.string.notificheGestore)
                                val importance = NotificationManager.IMPORTANCE_DEFAULT
                                val CHANNEL_ID = getString(R.string.notificheGestore)
                                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                                    description = descriptionText
                                }
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
                            } else {
                                Log.d("NOTIFICA", "<oreo")
                                val mBuilder = Notification.Builder(applicationContext)
                                        .setContentTitle("Consegna in arrivo")
                                        .setContentText("Seleziona un rider per questa consegna ")
                                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                        .setContentIntent(resultIntent)
                                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                                mBuilder.setAutoCancel(true)
                                notificationManager.notify(0, mBuilder.build())
                            }
                            }
                        }
                        e?.printStackTrace()
                    }
                }
            }
        })
    }

    private fun riderNotifications() {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val intent = Intent(applicationContext, RiderActivity::class.java)
        val resultIntent = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        checkRichiamato()
        val rider = FirebaseAuth.getInstance().currentUser!!.email.toString()
        db.collection("delivery").addSnapshotListener { snap, e ->
            if (snap != null) {
                for (doc in snap.documents) {
                    if (doc.id == rider) {
                        if (doc.getBoolean("sendNotR") == true) {
                            val updateSendNot = hashMapOf<String,Boolean>(
                                    "sendNotR" to false
                            )
                            db.collection("delivery").document(rider).set(updateSendNot, SetOptions.merge())

                            Log.d(TAG, "dentro createNotification")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val name = getString(R.string.notificheRider)
                                val descriptionText = getString(R.string.notificheRider)
                                val importance = NotificationManager.IMPORTANCE_DEFAULT
                                val CHANNEL_ID = getString(R.string.notificheRider)
                                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                                    description = descriptionText
                                }
                                notificationManager.createNotificationChannel(channel)
                                val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle("Ordine ASSEGNATO")
                                        .setContentText("Ti è stato assegnato un nuovo ordine.")
//                                               .setStyle(NotificationCompat.BigTextStyle()
//                                                       .bigText("Much longer text that cannot fit one line..."))
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(resultIntent)
                                with(NotificationManagerCompat.from(applicationContext)) {
                                    // notificationId is a unique int for each notification that you must define
                                    notify(3, builder.build())
                                }
                            } else {
                                Log.d("NOTIFICA", "<oreo")
                                val mBuilder = Notification.Builder(applicationContext)
                                        .setContentTitle("Ordine ASSEGNATO")
                                        .setContentText("Ti è stato assegnato un nuovo ordine.")
                                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                        .setContentIntent(resultIntent)
                                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                                mBuilder.setAutoCancel(true)
                                notificationManager.notify(0, mBuilder.build())
                            }
                        }
                    }
                }
            } else e?.printStackTrace()
        }
    }

    private fun createNotification(tipo: String) {
        when (tipo) {
            "Gestore" -> {
                gestoreNotifications()
            }
            "Cliente" -> {
                clienteNotifications()
            }
            "Rider" -> {
                riderNotifications()
            }
        }
    }

    private fun checkRichiamato() {
        val db = FirebaseFirestore.getInstance()
        val rider = FirebaseAuth.getInstance().currentUser?.email.toString()
        db.collection("delivery").addSnapshotListener { snap, e ->
            if (snap != null) {
                db.collection("delivery").document(rider).get()
                        .addOnSuccessListener {
                            if (it.getBoolean("richiamato") == true) {
                                val intent = Intent(applicationContext, RiderActivity::class.java)
                                intent.putExtra("richiamato", true)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
            }
        }
    }

    private fun getClients(myCallback: MyCallback) {
        val db = FirebaseFirestore.getInstance()
        val ris = mutableListOf<String>()
        db.collection("orders").get()
                .addOnSuccessListener {
                    for (d in it) {
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
                    for (d in it) {
                        ris.add(d.id)
                    }
                    myCallback.onCallback(ris)
                }
    }

    interface MyCallback {
        fun onCallback(ris: List<String>)
    }
}