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

class ChatNotificationService : Service() {
    var TAG = "chatNot"
    lateinit var notificationManager : NotificationManager
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Log.d(TAG,"onStart")
        return START_NOT_STICKY
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
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
        notificationManager.cancelAll()
        super.onDestroy()
    }

    private fun chatNotification(tipo : String) {
        val db = FirebaseFirestore.getInstance()
        val main = Intent(applicationContext, MainActivity::class.java)
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val resultIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(main)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        if (tipo == "Rider") {
            Log.d(TAG,"ramo then - Tipo vale $tipo")
            db.collection("chats").document(user).collection("contacts").addSnapshotListener { snap, e ->
                if (snap != null) {
                    for (d in snap.documents) {
                        db.collection("chats").document(d.id).collection("contacts").document(user).collection("messages").addSnapshotListener { snap, e ->
                            db.collection("chats").document(user).collection("contacts").document(d.id).addSnapshotListener { snapAcc, e ->
                                val ultimoAccesso = snapAcc!!.getTimestamp("ultimoaccesso")
                                val nome = snapAcc.getString("name")
                                val cognome = snapAcc.getString("surname")
                                if (snap != null) {
                                    for (mes in snap.documents) {
                                        if (mes.getTimestamp("ora") != null && ultimoAccesso != null) {
                                            if ((mes.getLong("inviato")!!.toInt() == 1) && (ultimoAccesso < mes.getTimestamp("ora")!!)) {
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
                                                            .setContentTitle(nome +" "+ cognome)
                                                            .setContentText(mes.getString("testo").toString())
                                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                            .setContentIntent(resultIntent)

                                                    with(NotificationManagerCompat.from(applicationContext)) {
                                                        notify(1, builder.build())
                                                    }
                                                }
                                                else {
                                                    Log.d("NOTIFICA", "<oreo")
                                                    val mBuilder = Notification.Builder(applicationContext)
                                                            .setContentTitle(nome + " " + cognome)
                                                            .setContentText(mes.getString("testo").toString())
                                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                    mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                                                    mBuilder.setAutoCancel(true)
                                                    notificationManager.notify(0, mBuilder.build())
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            //non rider
            Log.d(TAG,"ramo else - Tipo vale $tipo")
            db.collection("chats").document(user).collection("contacts").addSnapshotListener { snap, e ->
                if (snap != null) {
                    for (d in snap.documents) {
                        db.collection("chats").document(user).collection("contacts").document(d.id).collection("messages").addSnapshotListener { snap, e ->
                            if (snap != null) {
                                db.collection("chats").document(user).collection("contacts").document(d.id).addSnapshotListener { snapAcc, e ->
                                    if (snapAcc != null) {
                                        val ultimoAccesso = snapAcc.getTimestamp("ultimoaccesso")
                                        val nome = snapAcc.getString("name")
                                        val cognome = snapAcc.getString("surname")
                                        for (mes in snap.documents) {
                                            if (mes.getTimestamp("ora") != null && ultimoAccesso != null) {
                                                Log.d(TAG,"Dentro primo if")
                                                if ((mes.getLong("inviato")!!.toInt() == 0) && (ultimoAccesso < mes.getTimestamp("ora")!!)) {
                                                    Log.d(TAG,"xxx \n mes = ${mes.getTimestamp("ora")!!}\n ultimoAccesso = $ultimoAccesso \n uA < ts? = ${ultimoAccesso < mes.getTimestamp("ora")!!} \n messaggio vale ${mes.getString("testo")}")
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
                                                                .setContentTitle(nome + " " + cognome)
                                                                .setContentText(mes.getString("testo").toString())
                                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                                .setContentIntent(resultIntent)

                                                        with(NotificationManagerCompat.from(applicationContext)) {
                                                            notify(1, builder.build())
                                                        }
                                                    }
                                                    else {
                                                        Log.d("NOTIFICA", "<oreo")
                                                        val mBuilder = Notification.Builder(applicationContext)
                                                                .setContentTitle(nome + " " + cognome)
                                                                .setContentText(mes.getString("testo").toString())
                                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                                                        mBuilder.setAutoCancel(true)
                                                        notificationManager.notify(0, mBuilder.build())
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createNotification(tipo : String) {
        when (tipo) {
            "Gestore" -> {
                chatNotification(tipo)
            }
            "Cliente" -> {
                chatNotification(tipo)
            }
            "Rider" -> {
                chatNotification(tipo)
            }
        }
    }

    interface MyCallback{
        fun onCallback(ris: List<String>)
    }
}