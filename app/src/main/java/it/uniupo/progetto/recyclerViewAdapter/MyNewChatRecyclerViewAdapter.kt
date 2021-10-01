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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.ChatActivity
import it.uniupo.progetto.DatiPersonali.Utente
import it.uniupo.progetto.R
import java.util.*
import kotlin.collections.ArrayList

class MyNewChatRecyclerViewAdapter(private var riders: ArrayList<Utente>) : RecyclerView.Adapter<MyNewChatRecyclerViewAdapter.ViewHolder>() {
    lateinit var view : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_row, parent, false)
        val mail = view.findViewById<TextView>(R.id.mail).text.toString()

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = riders[position]
        holder.name.text = item.nome
        holder.surname.text = item.cognome
        holder.mail.text = item.email
        val contactElement = view.findViewById<RelativeLayout>(R.id.contact_element)
        contactElement.setOnClickListener{
            val dummy = hashMapOf<String, Any>(
                    "ora" to Timestamp(Date()),
                    "testo" to "Ciao sono il Gestore, per qualsiasi cosa scrivimi pure",
                    "inviato" to 1
            )
            val db = FirebaseFirestore.getInstance()
            db.collection("chats").document("gestore@gmail.com").collection("contacts").document(item.email).collection("messages").add(dummy)
            val intent = Intent(view.context,ChatActivity::class.java)
            intent.putExtra("mail",item.email)
            view.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = riders.size
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var surname: TextView = view.findViewById(R.id.surname)
        var name : TextView = view.findViewById(R.id.name)
        var mail : TextView = view.findViewById(R.id.mail)
    }

}
