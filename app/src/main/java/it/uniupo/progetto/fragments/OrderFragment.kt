
package it.uniupo.progetto.fragments
import it.uniupo.progetto.recyclerViewAdapter.*
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.DatiPersonali
import it.uniupo.progetto.NewChatActivity
import it.uniupo.progetto.R
import it.uniupo.progetto.StoricoOrdini
import it.uniupo.progetto.fragments.ProfileFragment.Azione
class OrderFragment  : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.order_fragment, container, false)
        var array = ArrayList<Azione>()
/*        array.add(Azione("Traccia il tuo ordine", 0))
        array.add(Azione("Chat con rider", 1))
        array.add(Azione("Richiama ordine", 2))
        array.add(Azione("Storico degli ordini", 3))*/
        // Inflate the layout for this fragment

      val history = view?.findViewById<RelativeLayout>(R.id.history)
        history!!.setOnClickListener{
            startActivity(Intent(view.context,StoricoOrdini::class.java))
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        getRiders(object: MyCallback {
           override fun onCallback(riders: ArrayList<Rider>) {
               riders.sortBy{it.distanza}
               recyclerView.adapter = MySelectRiderRecyclerViewAdapter(riders)
           }
        })
        return view

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
