package it.uniupo.progetto.recyclerViewAdapter

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.*
import it.uniupo.progetto.Consegna
import it.uniupo.progetto.R
import java.io.IOException


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyConsegneRecyclerViewAdapter(
    private val values: MutableList<Consegna>
) : RecyclerView.Adapter<MyConsegneRecyclerViewAdapter.ViewHolder>() {
    lateinit var view : View


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_consegne, parent, false)

        // link all'activity rider_delivery_info
        val info = view.findViewById<ImageButton>(R.id.info)
        info.setOnClickListener {
            var intent = Intent(parent.context, Rider_delivery_info::class.java)
            val orderId = view.findViewById<TextView>(R.id.orderId).text.toString()
            intent.putExtra("orderId",orderId)
            view.context.startActivity(intent)
        }
        val accept_order_button = view.findViewById<ImageButton>(R.id.check)
        accept_order_button.setOnClickListener{
            val userMail = view.findViewById<TextView>(R.id.userMail).text.toString()
            val orderId = view.findViewById<TextView>(R.id.orderId).text.toString()
            val address = view.findViewById<TextView>(R.id.indirizzo).text.toString()
            var geocodeMatches: List<Address>? = null

            try {
                geocodeMatches = Geocoder(view.context).getFromLocationName(address, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var zoomLevel = 11.0f
            var coord =LatLng(0.0, 0.0)
            for (mat in geocodeMatches!!) {
                coord = LatLng(mat.latitude, mat.longitude)
            }
            acceptOrder(userMail,orderId,coord.latitude,coord.longitude)


            var intent = Intent(parent.context, Rider_delivery_info::class.java)
            intent.putExtra("orderId",orderId)!!
            intent.putExtra("userMail",userMail)!!
            intent.putExtra("ordineAccettato",true)
            view.context.startActivity(intent)

//            startService(Intent(this,NotificationService::class.java))

        }


        val  refuse_order_button = view.findViewById<ImageButton>(R.id.deny)
        refuse_order_button.setOnClickListener{
            val userMail = view.findViewById<TextView>(R.id.userMail).text.toString()
            val orderId = view.findViewById<TextView>(R.id.orderId).text.toString()
            refuseOrder(userMail,orderId)
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]

        holder.indirizzo.text = item.posizione
        holder.tipo_pagamento.text = item.tipo_pagamento
        holder.distanza.text = view.context.getString(R.string.km, "%.2f".format(item.distanza))

        holder.userId.text = item.clientMail
        holder.orderId.text = item.orderId

       }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val indirizzo: TextView = view.findViewById(R.id.indirizzo)
        val tipo_pagamento: TextView = view.findViewById(R.id.pagamento)
        val distanza: TextView = view.findViewById(R.id.distanza)
        val userId: TextView = view.findViewById(R.id.userMail)
        val orderId: TextView = view.findViewById(R.id.orderId)

        override fun toString(): String {
            return super.toString()
        }
    }

    fun acceptOrder(user: String, orderId: String, latitude: Double, longitude: Double){
        val rider = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
         val det = hashMapOf<String, Any?>(
                "stato" to "accettato",
                "lat" to latitude,
                "lon" to longitude,
         )
        Log.d("DELIVERY - ",orderId)
        db.collection("delivery").document(rider).collection("orders").document(orderId).set(det, SetOptions.merge())
    }

    fun refuseOrder(user: String,orderId: String){
//        refuse order:
//        cambia stato in rifiutato
//        cancella nel fragment l'ordine
//
        val rider = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
        val det = hashMapOf<String, Any?>(
                "stato" to "rifiutato"
        )
        val dummy = hashMapOf<String,Any>(
                " " to " "
        )
        Log.d("DELIVERY - ",orderId)
        db.collection("assignedOrders").document(orderId).delete()
        db.collection("toassignOrders").document(orderId).set(dummy)
        db.collection("delivery").document(rider).collection("orders").document(orderId).set(det, SetOptions.merge())
        for(p in values){
            if(p.orderId==orderId){
                values.remove(p)
                notifyDataSetChanged()
            }
        }
//        db.collection("carts").document(user).collection("products").document(p.id.toString())
//            .delete()
//            .addOnSuccessListener {
//                Log.d("cart", "Eliminazione di $p.id avvenuta con successo")
//            }
//            .addOnFailureListener{
//                Log.d("cart", "Errore eliminazione di $p.id ")
//            }
    }

}