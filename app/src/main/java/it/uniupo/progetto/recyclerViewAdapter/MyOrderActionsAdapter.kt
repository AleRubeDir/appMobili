package it.uniupo.progetto.recyclerViewAdapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.ProfileFragment.Azione
import it.uniupo.progetto.*

class MyOrderActionsAdapter(private val array: ArrayList<Azione>) : RecyclerView.Adapter<MyOrderActionsAdapter.ViewHolder>() {
        lateinit var view : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                view = LayoutInflater.from(parent.context).inflate(R.layout.action, parent, false)
        val id  = view.findViewById<TextView>(R.id.id)
        val row = view.findViewById<RelativeLayout>(R.id.action_row)
        val nome  = view.findViewById<TextView>(R.id.nome)

        row.setOnClickListener {
            Log.d("prof", "Cliccato ${id.text} - ${nome.text} ")
            if (id.text == "0") {
            // Traccia il tuo ordine
                hasOrderPending(object : MyCallback2 {
                    override fun onCallback(ris: Boolean) {
                        if (ris) {
                            val intent = Intent(view.context, RiderPosition::class.java)
                            view.context.startActivity(intent)
                        } else Toast.makeText(view.context, "Non hai ordini attivi", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            if(id.text=="1"){
                //Chat con rider
                hasOrderPending(object : MyCallback2 {
                    override fun onCallback(ris: Boolean) {
                        if (ris) {
                            val mail = FirebaseAuth.getInstance().currentUser!!.email.toString()
                            getRiderForUser(mail,object : MyCallback{
                                override fun onCallback(rider: String) {
                                    Log.d("click","rider vale $rider mail vale $mail")
                                    val intent = Intent(view.context, ChatActivity::class.java)
                                    intent.putExtra("mail", rider )
                                    view.context.startActivity(intent)
                                }
                            })
                        } else Toast.makeText(view.context, "Non hai ordini attivi", Toast.LENGTH_SHORT).show()
                    }
                })

            }
            if(id.text=="2"){
                //Richiama ordine
                hasOrderPending(object : MyCallback2 {
                    override fun onCallback(ris: Boolean) {
                        if (ris) {
                            val intent = Intent(view.context, RichiamaOrdine::class.java)
                            view.context.startActivity(intent)
                            }
                        else Toast.makeText(view.context,"Non hai ordini attivi",Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            if(id.text=="3"){
                //Storico degli ordini
                val intent = Intent(view.context, StoricoOrdini::class.java)
                view.context.startActivity(intent)
            }
        }
        return ViewHolder(view)
    }


    fun hasOrderPending(myCallback: MyCallback2) {
                    val db = FirebaseFirestore.getInstance()
                    val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
                    db.collection("orders").document(email).collection("order").get()
                                .addOnSuccessListener {
                                    Log.d("richiama",it.isEmpty().toString())
                                    Log.d("richiama", "ecco it: $it")
                                    if(it.isEmpty){
                                        Toast.makeText(view.context, "Non esiste nessun ordine", Toast.LENGTH_SHORT).show()
                                    }
                                    for(d in it){
                                        Log.d("order","L'utente ha un ordine con rider ${d.id}")
                                        myCallback.onCallback(true)
                                    }
                                }
                                .addOnFailureListener{e->
                                    e.printStackTrace()
                                }
//        Toast.makeText(view.context, "Nessun ordine trovato, inizia ad acquistare!!!", Toast.LENGTH_SHORT).show()
    }


    private fun getRiderForUser(mail: String, myCallback: MyCallback) {
        val db = FirebaseFirestore.getInstance()
        Log.d("chat", mail)

        db.collection("client-rider").document(mail).collection("rider").get()
            .addOnSuccessListener {
                for(rider in it)   {
                    db.collection("delivery").document(rider.id).collection("orders").get()
                            .addOnSuccessListener {
                                for(d in it){
                                    if(d.getBoolean("leftMM")==false)  Toast.makeText(view.context, "Aspetta che il rider parta con il tuo ordine.", Toast.LENGTH_SHORT).show()
                                    else if(d.getBoolean("leftMM")==true) myCallback.onCallback(rider.id)
                                }
                            }

                }
            }

    }

    interface MyCallback {
        fun onCallback(rider: String)
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var text: TextView = view.findViewById(R.id.nome)
        var id : TextView = view.findViewById(R.id.id)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = array[position]
        holder.text.text = item.nome
        holder.id.text = item.id.toString()
    }
    override fun getItemCount(): Int = array.size

interface MyCallback2{
    fun onCallback(ris : Boolean)
}

}