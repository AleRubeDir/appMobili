package it.uniupo.progetto


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.*

class HomeActivity : AppCompatActivity() {
    lateinit var notificationManager : NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val chat = findViewById<ImageButton>(R.id.chat)

        val cartFragment = CartListFragment()
        val profileFragment = ProfileFragment()
        val shopFragment = ItemFragment()
        if(intent.getStringExtra("cart")=="vai"){
            makeCurrentFragment(cartFragment)
        }
        makeCurrentFragment(shopFragment)

        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.carrello -> makeCurrentFragment(cartFragment)
                R.id.profilo -> makeCurrentFragment(profileFragment)
                R.id.shop -> makeCurrentFragment(shopFragment)
            }
            true
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

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        replace(R.id.fl_wrapper, fragment)
        commit()
    }
   /*
   private fun createNotificationChannel(id: String, name: String, description: String) {
        var importance = 0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_LOW
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            var channel = NotificationChannel(id, name, importance)

            channel.description = description
            notificationManager?.createNotificationChannel(channel)
        }
    }
    fun sendNotification() {

        val notificationID = 101

        val resultIntent = Intent(this, HomeActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelID = "rider_leave"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationManager?.notify(notificationID,
                    Notification.Builder(this@HomeActivity,
                            channelID)
                            .setContentTitle("Example Notification")
                            .setContentText("This is an  example notification.")
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setChannelId(channelID)
                            .setContentIntent(pendingIntent)
                            .build()
            )
        }
    }
*/

    interface MyCallback {
        fun onCallback(ris : Boolean)
    }
    companion object    {
        var array: ArrayList<Prodotto> = ArrayList()
        var carrello: ArrayList<Prodotto> = ArrayList()
        var tot = 0.0

    }


}

