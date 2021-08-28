package it.uniupo.progetto

import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.MySelectRiderRecyclerViewAdapter
import it.uniupo.progetto.fragments.OrderFragment
import it.uniupo.progetto.fragments.Rider

class AssegnaOrdine : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.assegna_ordine)
              val recyclerView = findViewById<RecyclerView>(R.id.list)
      recyclerView.layoutManager = LinearLayoutManager(this)

      getRiders(object: MyCallback {
         override fun onCallback(riders: ArrayList<Rider>) {
             riders.sortBy{it.distanza}
             recyclerView.adapter = MySelectRiderRecyclerViewAdapter(riders)
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
                        if(d.getLong("occupato")!!.toInt()==0){
                            val riderpos = Location("")
                            riderpos.latitude = d.getDouble("lat")!!
                            riderpos.longitude = d.getDouble("lon")!!
                            val market = Location("")
                            market.latitude =  44.994154
                            market.longitude =   8.565942

                            val dist = (market.distanceTo(riderpos)/1000).toDouble()
                            val rider = Rider(d.getString("nome")!!,d.getString("cognome")!!,riderpos.latitude,riderpos.longitude,dist)
                            riders.add(rider)
                        }
                    }
                    mycallback.onCallback(riders)
                }
    }
    class Rider( var nome : String, var cognome : String , var lat : Double, var lon : Double, var distanza : Double){
        override fun toString(): String {
            return "RIDER\n nome : $nome \n cognome : $cognome \n lat : $lat \n lon : $lon \n distanza : $distanza \n"
        }
    }
}