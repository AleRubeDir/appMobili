package it.uniupo.progetto.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.*
import kotlin.collections.ArrayList
import it.uniupo.progetto.recyclerViewAdapter.*

class ChatGestoreFragment : Fragment() {
    var contacts = ArrayList<Contatto>()
    var chats= ArrayList<Chat>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_gestore, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.chats)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val new = view.findViewById<Button>(R.id.newChat)
        new.setOnClickListener{
            startActivity(Intent(view.context,NewChatActivity::class.java))
        }
        getUserContacts((object: MyCallbackContact{

            override fun onCallback(value: ArrayList<Contatto>) {

                contacts = value
                for (c in contacts) {

                    getMessageFromChat((object: MyCallbackMessages{

                        override fun onCallback(value: ArrayList<Messaggio>, notifications: Int, clientMail: Contatto?) {
                            val chatUtente = Chat(c,value,notifications)
                            chats.add(chatUtente)
                            chats.sortByDescending { it.messaggio.last().ora.seconds }
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
        db.collection("chats").document(user!!).collection("contacts").document(you).collection("messages")
                .get()
                .addOnSuccessListener { result ->
                    var messages = arrayListOf<Messaggio>()
                    for(document in result){
                    val mess = Messaggio(document.getLong("inviato")!!.toInt(), document.get("ora") as Timestamp,document.get("testo").toString())
                        messages.add(mess)
                        }
                    var notifications = 0
                    FirebaseFirestore.getInstance().collection("chats").document(user.toString()).collection("contacts").document(you).get().addOnSuccessListener {
                        notifications = it.getLong("notifications")!!.toInt()
                    }
                    myCallback.onCallback(messages, notifications,null)
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
    interface MyCallback {
        fun onCallback(value: List<Chat>)
    }
    interface MyCallbackContact{
        fun onCallback(value: ArrayList<Contatto>)
    }
    interface MyCallbackMessages{
        fun onCallback(value: ArrayList<Messaggio>, notifications: Int, clientMail: Contatto? )
    }
}