package it.uniupo.progetto

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uniupo.progetto.fragments.ProfileFragment
import it.uniupo.progetto.fragments.Rider_ConsegneFragment
import it.uniupo.progetto.fragments.Rider_chatFragment


class RiderActivity : AppCompatActivity() {
 fun stampaArray(array: ArrayList<Prodotto>){
        Log.d("totale", "RiderActivity- array -> $array")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider)
        val chatFragment = Rider_chatFragment()
        val deliveryFragment = Rider_ConsegneFragment()
        val profileFragment = ProfileFragment()
        makeCurrentFragment(deliveryFragment)
        Log.d("notifications","start notification service ")
        startService(Intent(this,NotificationService::class.java))
        // inizializza valori in caso di ordine accettato

        if(intent.getBooleanExtra("ordineAccettato",false)){
            ind = intent.getStringExtra("address")
            ordId = intent.getStringExtra("orderId")
            userMail = intent.getStringExtra("userMail")
            flag_consegna = 1
            makeCurrentFragment(Rider_ConsegneFragment())
        }


        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.chat -> {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val name = getString(R.string.notificheRider)
                        val descriptionText = "nuovoOrdine"
                        val importance = NotificationManager.IMPORTANCE_DEFAULT
                        val CHANNEL_ID = getString(R.string.notificheRider)
                        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                            description = descriptionText
                        }
                        // Register the channel with the system
                        val notificationManager: NotificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.createNotificationChannel(channel)

                        var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Ordine ASSEGNATO")
                                .setContentText("Ti è stato assegnato un nuovo ordine.")
//                                                .setStyle(NotificationCompat.BigTextStyle()
//                                                        .bigText("Much longer text that cannot fit one line..."))

                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)


                        with(NotificationManagerCompat.from(applicationContext)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(1234, builder.build())
                        }
                    }
                    makeCurrentFragment(chatFragment)

                }
                R.id.consegne -> {
                    makeCurrentFragment(deliveryFragment)
                }
                R.id.profilo -> {
                    makeCurrentFragment(profileFragment)
                }
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        replace(R.id.fl_wrapper, fragment)
        commit()
    }
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    companion object {
        var flag_consegna = 0
        var ind : String? = null
        var ordId : String? = null
        var userMail : String? = null
    }

}

