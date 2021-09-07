package it.uniupo.progetto.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Rider_chatFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_rider_list, container, false)
        // prendere le chat db
        //popolare fragment_chat_rider_list
        getChats(object : ChatGestoreFragment.MyCallbackMessages {
            override fun onCallback(value: ArrayList<Messaggio>, notifications: Int, contatto: Contatto?) {

                val chatUtente = Chat(contatto!!,value,notifications)
                val name = view.findViewById<TextView>(R.id.name)
                val surname = view.findViewById<TextView>(R.id.surname)
                val mail = view.findViewById<TextView>(R.id.mail)
                val notifiche = view.findViewById<TextView>(R.id.notifiche)
                val ora = view.findViewById<TextView>(R.id.ora)
                val data = view.findViewById<TextView>(R.id.data)
                val anteprima = view.findViewById<TextView>(R.id.anteprima)

                name.text = chatUtente.contatto.nome
                surname.text = chatUtente.contatto.cognome
                mail.text = chatUtente.contatto.mail

                if(value.size > 0 ){

                Log.d("chatRider", "valore " + (value.size))
                //Log.d("chatRider", "valore " + (value[value.size].ora.toString()))

                notifiche.text = notifications.toString()
                ora.text = (value[value.size].ora).toString()
                data.text = convertLongToTime(value[value.size].ora.seconds)
                anteprima.text = value[value.size].testo
                }

            }

        })
        return view
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time*1000)
        //  Log.d("mess","time vale $time date vale $date")
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }

    private fun getChats(myCallback: ChatGestoreFragment.MyCallbackMessages) {
        var db = FirebaseFirestore.getInstance()
        val usr = FirebaseAuth.getInstance().currentUser!!.email.toString()
        db.collection("rider-cliente_chat").document(usr).get()
                .addOnSuccessListener {
                    var clientMail = it.getString("mailClient")!!
                    db.collection("chats").document(clientMail).collection("contacts").document(usr).collection("messages").get()
                            .addOnSuccessListener { result ->
                                var messages = arrayListOf<Messaggio>()
                                for (document in result) {
                                    val mess = Messaggio(document.getLong("inviato")!!.toInt(), document.get("ora") as Timestamp, document.get("testo").toString())
                                    messages.add(mess)
                                }
                                var notifications = 0
                                var surname = ""
                                var name = ""
                                var tipo = ""
                                FirebaseFirestore.getInstance().collection("chats").document(clientMail).collection("contacts").document(usr).get().addOnSuccessListener {
                                    notifications = it.getLong("notifications")!!.toInt()
                                    name = it.getString("name")!!
                                    surname = it.getString("surname")!!
                                    tipo = it.getString("tipo")!!
                                }
                                var contatto = Contatto(clientMail,name,surname,tipo)
                                myCallback.onCallback(messages, notifications,contatto)
                            }



                }

    }


//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_chat_rider_list, container, false)
//        val recyclerView = view.findViewById<RecyclerView>(R.id.chats)
//        recyclerView.layoutManager = LinearLayoutManager(view.context)
//
//        val new = view.findViewById<Button>(R.id.newChat)
//        new.setOnClickListener {
//            startActivity(Intent(view.context, NewChatActivity::class.java))
//        }
//        getUserContacts((object : ChatGestoreFragment.MyCallbackContact {
//            override fun onCallback(value: ArrayList<Contatto>) {
//                contacts = value
//                for (c in contacts) {
//                    getMessageFromChat((object : ChatGestoreFragment.MyCallbackMessages {
//                        override fun onCallback(value: ArrayList<Messaggio>, notifications: Int) {
//                            val chatUtente = Chat(c, value, notifications)
//                            chats.add(chatUtente)
//                            chats.sortByDescending { it.messaggio.last().ora.seconds }
//                            recyclerView.adapter = MyChatGestoreRecyclerViewAdapter(chats)
//                        }
//                    }), c.mail)
//                }
//
//            }
//        })
//        )
//
//
//        return view
//    }
//
//    fun getMessageFromChat(myCallback: ChatGestoreFragment.MyCallbackMessages, you: String) {
//
//        val user = FirebaseAuth.getInstance().currentUser!!.email
//        val db = FirebaseFirestore.getInstance()
//        db.collection("chats").document(user!!).collection("contacts").document(you).collection("messages")
//                .get()
//                .addOnSuccessListener { result ->
//                    var messages = arrayListOf<Messaggio>()
//                    for (document in result) {
//                        val mess = Messaggio(document.getLong("inviato")!!.toInt(), document.get("ora") as Timestamp, document.get("testo").toString())
//                        messages.add(mess)
//                    }
//                    var notifications = 0
//                    FirebaseFirestore.getInstance().collection("chats").document(user.toString()).collection("contacts").document(you).get().addOnSuccessListener {
//                        notifications = it.getLong("notifications")!!.toInt()
//                    }
//                    myCallback.onCallback(messages, notifications)
//                }
//
//    }
}