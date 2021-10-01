package it.uniupo.progetto

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.ProfileFragment
import it.uniupo.progetto.fragments.Rider_ConsegneFragment
import it.uniupo.progetto.fragments.Rider_chatFragment


class RiderActivity : AppCompatActivity() {
    private lateinit var sp: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider)
        val chatFragment = Rider_chatFragment()
        val deliveryFragment = Rider_ConsegneFragment()
        val profileFragment = ProfileFragment()
        makeCurrentFragment(deliveryFragment)
        sp = applicationContext.getSharedPreferences("ordineAccettato", 0)
        Log.d("notifications","start notification service ")
        startService(Intent(this,NotificationService::class.java))
        Log.d("notifications","start chat notification service ")
        startService(Intent(this,ChatNotificationService::class.java))
        Log.d("notifications","start positionRider service ")
        startService(Intent(this,PositionService::class.java))
        Log.d("sharedPref", " fuori vale ${sp.getBoolean("ordineAccettato", false)} e " +
                "delete vale ${intent.getBooleanExtra("deletePrefOrd",false)}")

//        richiamato serve quando un cliente richiama un ordine, arriva da positionService
        if(!intent.getBooleanExtra("richiamato",false )) {
            if (intent.getBooleanExtra("deletePrefOrd", false)) {
                val editor = sp.edit()
                editor.putBoolean("ordineAccettato", false)
                editor.apply()
            }
            else if (intent.getBooleanExtra("ordineAccettato", false)) {
                val editor = sp.edit()
                editor.putBoolean("ordineAccettato", true)
                ind = intent.getStringExtra("address")
                ordId = intent.getStringExtra("orderId")
                userMail = intent.getStringExtra("userMail")
                flag_consegna = 1
                editor.putString("address", ind)
                editor.putString("ordId", ordId)
                editor.putString("userMail", userMail)
                editor.putInt("flag_consegna", flag_consegna)
                editor.apply()
                makeCurrentFragment(Rider_ConsegneFragment())
            }
            else if (sp.getBoolean("ordineAccettato", false) && (!intent.getBooleanExtra("deletePrefOrd", false))) {
                ind = sp.getString("address", "")
                ordId = sp.getString("ordId", "")
                userMail = sp.getString("userMail", "")
                flag_consegna = sp.getInt("flag_consegna", -1)
                makeCurrentFragment(Rider_ConsegneFragment())
            }


        }else{

            AlertDialog.Builder(this)
                    .setTitle("Ordine richiamato")
                    .setMessage("Non dovrai più consegnare questo ordine")
                    .setPositiveButton("Accetta")
                    { _: DialogInterface, _: Int ->
                        makeCurrentFragment(chatFragment)
                    }
                    .show()
            ind = ""
            ordId = ""
            userMail = ""
            flag_consegna = 0

//            cancella ordine da delivery e da order
            val db = FirebaseFirestore.getInstance()
            val usr = FirebaseAuth.getInstance().currentUser!!.email.toString()
            db.collection("delivery").document(usr).collection("orders").get()
                    .addOnSuccessListener {
                        for(d in it){
                            d.reference.delete()
                        }
                    }
            db.collection("delivery").document(usr).collection("richiamato").document("r").delete()

            db.collection("delivery").document(usr).delete()
        }
        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.chat -> {
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
        var partenzaMMVisibility = View.VISIBLE
        var confermaPagamentovisibility = View.INVISIBLE
        var rifiutaPagamentovisibility = View.INVISIBLE
    }

}

