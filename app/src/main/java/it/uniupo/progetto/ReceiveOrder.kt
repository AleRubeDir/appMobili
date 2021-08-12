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
                    /*   hasOrderPending(object : MyCallback {
       override fun onCallback(ris: Boolean) {
           if(ris) {
               chat.visibility = View.VISIBLE
               chat.setOnClickListener {
                   var i = Intent(this@HomeActivity, ChatActivity::class.java)
                   startActivity(i)
               }
           }
               else{
                   chat.visibility = View.INVISIBLE
               }
           }
   })*/


                    /*  manda notifica, da mettere quando rider accetta ordine
                    val intent = Intent(this, HomeActivity::class.java)
                      var rider = FirebaseAuth.getInstance().currentUser!!.email.toString()
                      intent.putExtra("rider",rider)
                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                      var pendInt = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                     notificationManager =   getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                      var builder = NotificationCompat.Builder(this)
                              .setContentTitle("Consegna in arrivo")
                              .setContentText("Hey! Il rider è partito con la tua consegna!")
                              .setContentIntent(pendInt)
                              .setSmallIcon(R.drawable.rider)
                              .setAutoCancel(true)
                              .build()
                      val not = findViewById<Button>(R.id.notifica)
                      not.setOnClickListener{
                          notificationManager.notify(0, builder)
                      }*/

                }

        /* private fun hasOrderPending(myCallback: MyCallback) {
             val db = FirebaseFirestore.getInstance()
             val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
             db.collection("orders").get()
                     .addOnSuccessListener { doc ->
                         for(d in doc){
                             if(d.id==email){
                                 Log.d("order","L'utente ha un ordine $doc ")
                                 myCallback.onCallback(true)
                             }
                         }

                     }
                     .addOnFailureListener{e->
                         e.printStackTrace()
                     }
         }
     */
                }
    }
