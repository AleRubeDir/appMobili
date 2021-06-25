package it.uniupo.progetto.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatGestoreFragment : Fragment() {
    var messages= ArrayList<Messaggio>()
    var contacts = ArrayList<Contatto>()
    var chats= ArrayList<Chat>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_gestore, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.chats)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        Log.d("Chats","prova")
        getUserContacts((object: MyCallbackContact{
            override fun onCallback(value: ArrayList<Contatto>) {
                Log.d("Chats","value vale $value")
                contacts = value

                for (c in contacts) {
                    getMessageFromChat((object: MyCallbackMessages{
                        override fun onCallback(value: ArrayList<Messaggio>) {
                            Log.d("Chats","---Value vale $value")
                            messages = value
                            Log.d("Chats","---Mess copiato vale $messages")
                            chats.add(Chat(c,messages))
                            chats.add(Chat(c,messages))
                            chats.add(Chat(c,messages))
                            chats.add(Chat(c,messages))
                            chats.add(Chat(c,messages))

                            recyclerView.adapter = MyChatGestoreRecyclerViewAdapter(chats)
                        }
                    }),c.mail)
                }
            }
        })
        )


        return view
    }

    fun getMessageFromChat(myCallback: MyCallbackMessages,you: String){
        val user = FirebaseAuth.getInstance().currentUser!!.email

        val db = FirebaseFirestore.getInstance()
        messages.clear()
        db.collection("chats").document(user!!).collection("contacts").document(you).collection("messages")
                .get()
                .addOnSuccessListener { result ->
                    for(document in result){
                        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                    val mess = Messaggio(document.getLong("inviato")!!.toInt(), document.get("ora") as Timestamp,document.get("testo").toString())
                        Log.d("Chats","mess $mess")
                        messages.add(mess)
                    }
                    myCallback.onCallback(messages)
                }
    }
    private fun getUserContacts(myCallback: MyCallbackContact) {
        val user = FirebaseAuth.getInstance().currentUser!!.email

        val db = FirebaseFirestore.getInstance()
        db.collection("chats").document(user!!).collection("contacts")
            .get()
            .addOnSuccessListener { result->
                for(document in result){
                    contacts.add(Contatto(document.get("mail").toString(), document.get("name").toString(), document.get("surname").toString(), document.get("tipo").toString()))
                }
                myCallback.onCallback(contacts)
                Log.d("chats","Contatti recuperati con successo")
            }
    }
 /*   private fun getUserChat(myCallback: MyCallback ) {
        val user = FirebaseAuth.getInstance().currentUser!!.email

        val db = FirebaseFirestore.getInstance()
        db.collection("chats").document(user!!).collection("contacts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("chats","GUC -----")
                    Log.d("chats","${document}")
                    val inviato = document.getLong("inviato")!!.toInt()
                    val ora = document.get("ora") as Date
                    val testo = document.get("testo").toString()
                    val mess = Messaggio(inviato,ora,testo)
                    Log.d("chats","GUC -----")
                }
                myCallback.onCallback(chats)
            }
            .addOnFailureListener {
                Log.d("totale", "Error getting document - get user cart()")
            }
    }*/
    interface MyCallback {
        fun onCallback(value: List<Chat>)
    }
    interface MyCallbackContact{
        fun onCallback(value: ArrayList<Contatto>)
    }
    interface MyCallbackMessages{
        fun onCallback(value: ArrayList<Messaggio>)
    }
}