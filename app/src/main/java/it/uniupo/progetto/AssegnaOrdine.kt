package it.uniupo.progetto

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.MySelectRiderRecyclerViewAdapter

class AssegnaOrdine : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.assegna_ordine)
              val recyclerView = findViewById<RecyclerView>(R.id.list)
      recyclerView.layoutManager = LinearLayoutManager(this)
        val ordId = intent.getStringExtra("ordId")
        val clientMail = intent.getStringExtra("client")
        val tipo = intent.getStringExtra("tipo")
        val distanza = intent.getDoubleExtra("distanza",0.0)
      getRiders(object: MyCallback {
         override fun onCallback(riders: ArrayList<Rider>) {
             riders.sortBy{it.distanza}
             recyclerView.adapter = MySelectRiderRecyclerViewAdapter(distanza,riders,ordId,clientMail,tipo)
         }
      })

    }
    interface MyCallback{
        fun onCallback(rider: ArrayList<Rider>)
    }
    private fun getRiders(mycallback : MyCallback){
        val db = FirebaseFirestore.getInstance()
        var riders = arrayListOf<Rider>()
        db.collection("riders").get()
                .addOnSuccessListener {
                    for(d in it){
                        if(!d.getBoolean("disponibile")!!){
                            val riderpos = Location("")
                            riderpos.latitude = d.getDouble("lat")!!
                            riderpos.longitude = d.getDouble("lon")!!
                            val market = Location("")
                            market.latitude =  44.994154
                            market.longitude =   8.565942

                            val dist = (market.distanceTo(riderpos)/1000).toDouble()
                            val rider = Rider(d.id,d.getString("nome")!!,d.getString("cognome")!!,riderpos.latitude,riderpos.longitude,dist)
                            riders.add(rider)
                        }
                    }
                    mycallback.onCallback(riders)
                }
    }
    class Rider(var mail : String , var nome : String, var cognome : String , var lat : Double, var lon : Double, var distanza : Double){
        override fun toString(): String {
            return "RIDER \n mail : $mail \n nome : $nome \n cognome : $cognome \n lat : $lat \n lon : $lon \n distanza : $distanza \n"
        }
    }
}