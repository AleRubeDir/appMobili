package it.uniupo.progetto.recyclerViewAdapter

import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.*
import it.uniupo.progetto.Consegna
import it.uniupo.progetto.R
import java.util.*

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
            val address = view.findViewById<TextView>(R.id.indirizzo).text.toString()
            intent.putExtra("orderId",orderId)
            intent.putExtra("address",address)
            view.context.startActivity(intent)
        }
        val accept_order_button = view.findViewById<ImageButton>(R.id.check)
        accept_order_button.setOnClickListener{
            val userMail = view.findViewById<TextView>(R.id.userMail).text.toString()
            val orderId = view.findViewById<TextView>(R.id.orderId).text.toString()
            val address = view.findViewById<TextView>(R.id.indirizzo).text.toString()
            acceptOrder(orderId,userMail)
            val intent = Intent(parent.context, RiderActivity::class.java)
            intent.putExtra("address",address)
            intent.putExtra("orderId",orderId)
            intent.putExtra("userMail",userMail)
            intent.putExtra("ordineAccettato",true)
            view.context.startActivity(intent)
        }

        val  refuse_order_button = view.findViewById<ImageButton>(R.id.deny)
        refuse_order_button.setOnClickListener{
            val orderId = view.findViewById<TextView>(R.id.orderId).text.toString()
            refuseOrder(orderId)
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

    }
    private fun getUserData(user : String ,myCallback: DatiPersonali.MyCallback){
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user)
                .get()
                .addOnSuccessListener { result->
                    Log.d("prof","$result")
                    lateinit var u : DatiPersonali.Utente
                    if(result.id == user){
                        u = DatiPersonali.Utente(
                                result.get("mail").toString(),
                                result.get("name").toString(),
                                result.get("surname").toString(),
                                result.get("type").toString(),
                                result.get("address").toString()
                        )
                        Log.d("prof","$u")
                        myCallback.onCallback(u)
                    }
                }
                .addOnFailureListener{ e -> Log.w("---","Error getting user info - DatiPersonali",e)}
    }

    fun acceptOrder(orderId: String, userMail: String){
        val rider = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()

         val det = hashMapOf<String, Any?>(
                "stato" to -1,
         )
        Log.d("DELIVERY - ",orderId)
        db.collection("delivery").document(rider).collection("orders").document(orderId).set(det, SetOptions.merge())
        //corrispondenza rider-client
        getUserData(userMail, object : DatiPersonali.MyCallback {
            override fun onCallback(u: DatiPersonali.Utente) {
                val chat = hashMapOf<String, Any?>(
                        "name" to u.nome,
                        "surname" to u.cognome,
                        "mail" to userMail,
                        "notifications" to 0
                )
                db.collection("chats").document(rider).collection("contacts").document(userMail).set(chat, SetOptions.merge())
            }

        })

        val dummy = hashMapOf<String, Any?>(
                " " to " ",
        )
        db.collection("chats").document(rider)
                .set(
                        dummy,
                        SetOptions.merge()
                )


    }

//        refuse order:
    fun refuseOrder(orderId: String){
        val rider = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
//      toglie da assignedOrders, mette in toassignOrders
        Log.d("DELIVERY - ",orderId)
        db.collection("assignedOrders").document(orderId).get()
            .addOnCompleteListener {
                val tipoPagamento = it.result.getString("tipo").toString()
                val indirizzo = it.result.getString("indirizzo").toString()
                val cliente = it.result.getString("cliente").toString()
                val dummy = hashMapOf<String, Any?>(
                    "tipo" to tipoPagamento,
                    //indirizzo ordine
                    "indirizzo" to indirizzo,
                    "cliente" to cliente
                )
                db.collection("toassignOrders").document(orderId).set(dummy)
            }
        db.collection("assignedOrders").document(orderId).delete()
//      toglie da assignedOrders, mette in toassignOrders
//        cancella nel fragment l'ordine
        for(p in values){
            if(p.orderId==orderId){
                values.remove(p)
                notifyDataSetChanged()
            }
        }
//        cancella nel fragment l'ordine

        //cancella ordine nel db delivery
        db.collection("delivery").document(rider).collection("orders").document(orderId).get()
            .addOnCompleteListener {
                val cliente = it.result.getString("client").toString()
                val distanza = it.result.getDouble("distanza")
                /*var statoOrdine = it.result.getLong("stato")!!.toInt()*/
                val tipo_pagamento = it.result.getString("tipo_pagamento").toString()
                val entry = hashMapOf<String, Any?>(
                    "data" to  Date(),
                    "tipoPagamento" to tipo_pagamento,
                    "distanza" to distanza,
                    "cliente" to cliente,
                    "orderId" to orderId,
                    "ratingC" to -1,
                    "ratingQ" to-1,
                    "ratingV" to -1,
                    "rider" to rider,
                    "risultatoOrdine" to -2,
                    "statoPagamento" to -2,
                )
                db.collection("orders_history").document(orderId).set(entry, SetOptions.merge())
                        .addOnSuccessListener {
                            db.collection("delivery").document(rider).collection("orders").document(orderId).delete()
                            db.collection("toassignOrders").document(orderId).delete()
                        }
            }

        //cancella ordine nel db delivery
        //rider torna disponibile
        val disponibile = hashMapOf<String, Any?>(
         "disponibile" to true
        )
        db.collection("riders").document(rider).set(disponibile, SetOptions.merge())
    }

}