package it.uniupo.progetto

import android.app.NotificationManager
import android.app.PendingIntent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore

class ReceiveOrder :  AppCompatActivity() {
    lateinit var notificationManager : NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gestore_prodotto)
        checkOrder()
    }
    private fun checkOrder(){
        val db = FirebaseFirestore.getInstance()
        db.collection("orders")
                .addSnapshotListener{ e,snap ->
                    if(snap!=null) {
                        var pendInt = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        var builder = NotificationCompat.Builder(this)
                                .setContentTitle("Consegna in arrivo")
                                .setContentText("Seleziona il rider per questa consegna")
                                .setContentIntent(pendInt)
                                .setSmallIcon(R.drawable.cart)
                                .setAutoCancel(true)
                                .build()
                        notificationManager.notify(0, builder)
                    }

                }
                }
    }
