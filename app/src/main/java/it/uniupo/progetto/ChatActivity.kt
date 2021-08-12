package it.uniupo.progetto

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.ChatGestoreFragment
import it.uniupo.progetto.recyclerViewAdapter.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private var messages = ArrayList<Messaggio>()
    lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        val contatto = findViewById<TextView>(R.id.contatto)

        val mail = intent.getStringExtra("mail")!!.toString()
        Log.d("chats", mail)
        getMessageFromChat((object : ChatGestoreFragment.MyCallbackMessages {
            override fun onCallback(value: ArrayList<Messaggio>) {
                Log.d("Chats", "Dentro la chat $messages")
                val db = FirebaseFirestore.getInstance()

                val user = FirebaseAuth.getInstance().currentUser!!.email
                createChat(contatto,user,mail)
                db.collection("chats").document(user!!).collection("contacts").document(mail).collection("messages")
                        .addSnapshotListener{e,snap->
                            if(snap!=null){
                                recyclerView = findViewById<RecyclerView>(R.id.messages)
                                recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
                                Log.d("mymess", "$messages")
                                recyclerView.adapter = MyMessageListRecyclerViewAdapter(messages)
                            }
                        }
                recyclerView = findViewById<RecyclerView>(R.id.messages)
                recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
                Log.d("mymess", "$messages")
                recyclerView.adapter = MyMessageListRecyclerViewAdapter(messages)
            }
        }), mail)

        var user = FirebaseAuth.getInstance().currentUser!!.email

        val send = findViewById<Button>(R.id.send)
        val mess = findViewById<EditText>(R.id.write_message)
        send.setOnClickListener{
            if(!mess.text.toString().isNullOrBlank()){
                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                var ora  = Timestamp(Date())
               /* var ora = Calendar.getInstance().time
                ora = sdf.parse(ora.toString())*/
             //   if(minuto.length==1) minuto = "0"+minuto
                val messaggio = Messaggio(1, ora, mess.text.toString())
                if (user != null) {
                    sendMessage(messaggio, mail, user)
                    messages.add(messaggio)
                }
            }
            mess.text.clear()
            recyclerView.adapter = MyMessageListRecyclerViewAdapter(messages)
        }
    }

    private fun createChat(contatto: TextView, user: String?, mail: String) {
        //user è il cliente
        //mail è il rider
        val db = FirebaseFirestore.getInstance()
        var check = 0
        getUserData(mail, object: DatiPersonali.MyCallback {
            override fun onCallback(u: DatiPersonali.Utente) {
                db.collection("chats").get()
                    .addOnSuccessListener {
                        it.forEach { doc ->
                            if (doc.id == user) check = 1
                        }
                        contatto.text=u.nome + " " + u.cognome
                        if (check == 0) {
                            val entry = hashMapOf<String, Any?>(
                                "name" to u.nome,
                                "surname" to u.cognome,
                                "mail" to user,
                                "tipo" to u.tipo
                            )
                            db.collection("chats").document(user!!).collection("contacts").document(mail)
                                .set(
                                    entry,
                                    SetOptions.merge()
                                )
                         //   db.collection("chats").document(user!!).collection("contacts").document(mail).messages()
                        }
                    }
            }
        })
    }
    private fun getUserData(user : String ,myCallback: DatiPersonali.MyCallback){
        var u = DatiPersonali.Utente("err", "err", "err", "err", "null")
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .get()
            .addOnSuccessListener { result->
                Log.d("prof","$result")
                for (document in result) {
                    if(document.id == user){
                        //utente ha già scelto il tipo di account
                        u = DatiPersonali.Utente(
                            document.get("mail").toString(),
                            document.get("name").toString(),
                            document.get("surname").toString(),
                            document.get("type").toString(),
                            document.get("address").toString()
                        )
                        Log.d("prof","$u")
                    }
                    myCallback.onCallback(u)
                }
            }
            .addOnFailureListener{ e -> Log.w("---","Error getting user info - DatiPersonali",e)}
    }
    private fun sendMessage(messaggio: Messaggio, you: String, me: String){
        val db = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String, Any>(
                "inviato" to messaggio.inviato,
                "ora" to messaggio.ora,
                "testo" to messaggio.testo
        )
        db.collection("chats").document(me).collection("contacts").document(you).collection("messages")
                .add(entry)
                .addOnSuccessListener {
                    Log.d("Chat", "Messaggio inviato con successo")
                }
                .addOnFailureListener{ e->
                    Log.d("Chat", "Errore invio messaggio $e")
                }
    }
    private fun getMessageFromChat(myCallback: ChatGestoreFragment.MyCallbackMessages, you: String){
        val user = FirebaseAuth.getInstance().currentUser!!.email

        val db = FirebaseFirestore.getInstance()
        messages.clear()
        db.collection("chats").document(user!!).collection("contacts").document(you).collection("messages")
                .get()
                .addOnSuccessListener { result ->
                    for(document in result){
                       /* val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                        var ora = Calendar.getInstance().time
                        ora = sdf.parse(document.get("ora").toString())*/

                        val mess = Messaggio(document.getLong("inviato")!!.toInt(), document.get("ora") as Timestamp, document.get("testo").toString())
                        Log.d("Chats", "mess $mess")
                        messages.add(mess)
                    }
                    myCallback.onCallback(messages)
                }
    }
}