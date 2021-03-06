package it.uniupo.progetto

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.ChatGestoreFragment
import it.uniupo.progetto.recyclerViewAdapter.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private var messages = ArrayList<Messaggio>()
    lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)
        val contatto = findViewById<TextView>(R.id.contatto)
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val ricevente = intent.getStringExtra("mail")!!.toString()
        deleteNotifications()
        var rider = 0
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val flag = intent.getBooleanExtra("fromRider", false)

        val accesso = hashMapOf<String,Any>(
                "ultimoaccesso" to Timestamp(Date())
        )
        Log.d("livenot","chat activity \n $accesso")
        FirebaseFirestore.getInstance().collection("chats").document(user).collection("contacts").document(ricevente).set(accesso, SetOptions.merge()).addOnSuccessListener {


        if (flag) {
            //sono un rider
            rider = 1
            getMessageFromChat((object:ChatGestoreFragment.MyCallbackMessages{
                override fun onCallback(value: ArrayList<Messaggio>, clientMail: Contatto?) {
                    Log.d("Chats", "Dentro la chat $messages")
                    Log.d("mymess", "user $user , mail $ricevente,")
                    createChat(contatto, user, ricevente)
                    recyclerView = findViewById(R.id.messages)
                    recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
                    recyclerView.scrollToPosition(value.size-1)
                    recyclerView.adapter = MyMessageListRecyclerViewAdapter(messages,rider)
                }
            }),ricevente ,user)
        } else {
            getMessageFromChat((object : ChatGestoreFragment.MyCallbackMessages {
                override fun onCallback(value: ArrayList<Messaggio>, clientMail: Contatto?) {
                    Log.d("Chats", "Dentro la chat $messages")
                    Log.d("mymess", "mail $ricevente, user $user")
                    createChat(contatto, user, ricevente)
                    recyclerView = findViewById(R.id.messages)
                    recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
                    recyclerView.scrollToPosition(value.size-1)
                    recyclerView.adapter = MyMessageListRecyclerViewAdapter(messages, rider)
                }
            }), user, ricevente)

     }
            val send = findViewById<ImageButton>(R.id.send)
            val mess = findViewById<EditText>(R.id.write_message)
            send.setOnClickListener {
                if (mess.text.toString().isNotBlank()) {
                    val ora = Timestamp(Date())
                    val messaggio : Messaggio
                    if(rider==1){
                        messaggio = Messaggio(0, ora, mess.text.toString())
                        //user = rider, ricevente = cliente
                        sendMessage(messaggio,ricevente,user)
                    }
                    else {
                        messaggio = Messaggio(1, ora, mess.text.toString())
                        //user = cliente, ricevente = rider
                        sendMessage(messaggio, user , ricevente )
                    }
                    messages.add(messaggio)
                }
                mess.text.clear()
                recyclerView.adapter = MyMessageListRecyclerViewAdapter(messages,rider)
            }
     }
    }

    fun deleteNotifications() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    private fun createChat(contatto: TextView, currUser: String?, ricevente: String) {
        //mail ?? il rider
        val db = FirebaseFirestore.getInstance()
        var check = 0
        getUserData(ricevente, object: DatiPersonali.MyCallback {
            override fun onCallback(u: DatiPersonali.Utente) {
                Log.d("prof","u vale $u")
                contatto.text=applicationContext.getString(R.string.nomeChat,u.nome,u.cognome)
                val empty = hashMapOf<String,Any>(
                        " " to " "
                )
                val entry = hashMapOf<String, Any?>(
                        "name" to u.nome,
                        "surname" to u.cognome,
                        "mail" to ricevente,
                        "tipo" to u.tipo
                )
                db.collection("chats").document(currUser.toString()).set(empty, SetOptions.merge())
                db.collection("chats").document(currUser.toString()).collection("contacts").document(u.email).set(entry, SetOptions.merge())
            }
        })
    }
    private fun getUserData(user : String ,myCallback: DatiPersonali.MyCallback){
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user)
                .get()
                .addOnSuccessListener { result->
                    Log.d("prof","${result.id} == ${user} ")
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
    private fun sendMessage(messaggio: Messaggio, me: String, ricevente: String){

        //me = CLIENTE, ricente = RIDER chat/cliente/contacts/rider
        Log.d("Chats","$ricevente riceve da  $me")
        val db = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String, Any>(
                "inviato" to messaggio.inviato,
                "ora" to messaggio.ora,
                "testo" to messaggio.testo
        )

        db.collection("chats").document(me).collection("contacts").document(ricevente).collection("messages")
                .add(entry)
                .addOnSuccessListener {
                    Log.d("Chat", "Messaggio inviato con successo")
                }
                .addOnFailureListener{ e->
                    Log.d("Chat", "Errore invio messaggio $e")
                }
    }
    private fun getMessageFromChat(myCallback: ChatGestoreFragment.MyCallbackMessages, user : String , you: String){
        Log.d("getmessage","user vale $user, you vale $you")
        val db = FirebaseFirestore.getInstance()
        db.collection("chats").document(user).collection("contacts").document(you).collection("messages").addSnapshotListener{snap,e->
            messages.clear()
            if(snap!=null){
                for(d in snap.documents) {
                    val mess = Messaggio(d.getLong("inviato")!!.toInt(), d.get("ora") as Timestamp, d.get("testo").toString())
                    messages.add(mess)
                }
            myCallback.onCallback(messages,null)
            }
        }
    }
}