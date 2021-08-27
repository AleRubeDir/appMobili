package it.uniupo.progetto.recyclerViewAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.*
import it.uniupo.progetto.ChooseActivity
import it.uniupo.progetto.Consegna
import it.uniupo.progetto.R


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyConsegneRecyclerViewAdapter(
    private val values: List<Consegna>
) : RecyclerView.Adapter<MyConsegneRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_consegne, parent, false)

        // link all'activity rider_delivery_info
        val info = view.findViewById<ImageButton>(R.id.info)
        info.setOnClickListener {
            view.context.startActivity(Intent(parent.context, Rider_delivery_info::class.java))
        }

        val accept_order_button = view.findViewById<ImageButton>(R.id.check)
        accept_order_button.setOnClickListener{
            val userMail = view.findViewById<TextView>(R.id.userMail).text.toString()
            val orderId = view.findViewById<TextView>(R.id.orderId).text.toString()
            acceptOrder(userMail,orderId)
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
        holder.distanza.text = "DISTANZA(TODO)"
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

    fun acceptOrder(user: String,orderId: String){
        val rider = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
         val det = hashMapOf<String, Any?>(
                "stato" to "accettato"
         )
        Log.d("DELIVERY - ",orderId)
        db.collection("delivery").document(rider).collection("client").document(user).collection("details").document(orderId).set(det, SetOptions.merge())
    }

    fun refuseOrder(user: String,orderId: String){
        val rider = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
        val det = hashMapOf<String, Any?>(
                "stato" to "rifiutato"
        )
        Log.d("DELIVERY - ",orderId)
        db.collection("delivery").document(rider).collection("client").document(user).collection("details").document(orderId).set(det, SetOptions.merge())
    }
}