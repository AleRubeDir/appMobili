package it.uniupo.progetto

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
        val mail = intent.getStringExtra("mail")!!.toString()
        Log.d("chats", mail)
        var flag_inviato = 1
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val flag = intent.getBooleanExtra("fromRider", false)

        Log.d("mymess","flag vale $flag")
        if (flag) {
            flag_inviato=0
            getMessageFromChat((object:ChatGestoreFragment.MyCallbackMessages{
                override fun onCallback(value: ArrayList<Messaggio>, notifications: Int, clientMail: Contatto?) {
                    Log.d("Chats", "Dentro la chat $messages")
                    Log.d("mymess", "user $user , mail $mail,")
                    createChat(contatto, user, mail)
                    recyclerView = findViewById(R.id.messages)
                    recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
                    recyclerView.adapter = MyMessageListRecyclerViewAdapter(messages,flag_inviato)
                }
            }),mail ,user)
        } else {
            getMessageFromChat((object : ChatGestoreFragment.MyCallbackMessages {
                override fun onCallback(value: ArrayList<Messaggio>, notifications: Int, clientMail: Contatto?) {
                    Log.d("Chats", "Dentro la chat $messages")
                    Log.d("mymess", "mail $mail, user $user")
                    createChat(contatto, user, mail)
                    recyclerView = findViewById(R.id.messages)
                    recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
                    recyclerView.adapter = MyMessageListRecyclerViewAdapter(messages,flag_inviato)
                }
            }),user , mail)


            val send = findViewById<Button>(R.id.send)
            val mess = findViewById<EditText>(R.id.write_message)
            send.setOnClickListener {
                if (mess.text.toString().isNotBlank()) {
                    val ora = Timestamp(Date())
                    val messaggio = Messaggio(1, ora, mess.text.toString())
                    sendMessage(messaggio, mail, user)
                    messages.add(messaggio)
                }
                mess.text.clear()
                recyclerView.adapter = MyMessageListRecyclerViewAdapter(messages,flag_inviato)
            }
        }
    }

    private fun createChat(contatto: TextView, cliente: String?, mail: String) {
        //mail è il rider
        val db = FirebaseFirestore.getInstance()
        var check = 0
               getUserData(mail, object: DatiPersonali.MyCallback {
            override fun onCallback(u: DatiPersonali.Utente) {
                db.collection("chats").document(cliente.toString()).collection("contacts").get()
                    .addOnSuccessListener {
                        it.forEach { doc ->
                            Log.d("mymess","${doc.id} == $cliente???")
                            if (doc.id == mail) check = 1

                        Log.d("mymess","$check")
                        contatto.text=applicationContext.getString(R.string.nomeChat,u.nome,u.cognome)
                        if (check == 0) {
                            Log.d("mymess","check vale $check")
                            val entry = hashMapOf<String, Any?>(
                                "name" to u.nome,
                                "surname" to u.cognome,
                                "mail" to mail,
                                "tipo" to u.tipo
                            )
                            Log.d("mymess","$entry")
                            db.collection("chats").document(cliente!!).collection("contacts").document(mail)
                                .set(
                                    entry,
                                    SetOptions.merge()
                                )
                            }
                        }
                    }
            }
        })
    }
    private fun getUserData(user : String ,myCallback: DatiPersonali.MyCallback){
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result->
                Log.d("prof","$result")
                for (document in result) {
                    lateinit var u : DatiPersonali.Utente
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
                        myCallback.onCallback(u)
                    }

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
        db.collection("chats").document(me).collection("contacts").document(you).get()
                .addOnSuccessListener {
                   var not = it.getLong("notifications")!!.toInt()
                    val notify = hashMapOf<String, Any>(
                            "notifications" to not+1
                    )
                    Log.d("notify","$not")
                    db.collection("chats").document(me).collection("contacts").document(you).set(notify, SetOptions.merge())
                }

        db.collection("chats").document(me).collection("contacts").document(you).collection("messages")
                .add(entry)
                .addOnSuccessListener {
                    Log.d("Chat", "Messaggio inviato con successo")
                }
                .addOnFailureListener{ e->
                    Log.d("Chat", "Errore invio messaggio $e")
                }
    }
    private fun getMessageFromChat(myCallback: ChatGestoreFragment.MyCallbackMessages, user : String , you: String){
        val db = FirebaseFirestore.getInstance()
        messages.clear()
        val entry = hashMapOf<String, Any?>(
                "notifications" to 0
        )
        db.collection("chats").document(user!!).collection("contacts").document(you).set(entry,SetOptions.merge())

        db.collection("chats").document(user).collection("contacts").document(you).collection("messages")
                .get()
                .addOnSuccessListener { result ->
                    for(document in result){
                        val mess = Messaggio(document.getLong("inviato")!!.toInt(), document.get("ora") as Timestamp, document.get("testo").toString())
                        Log.d("Chats", "mess $mess")
                        messages.add(mess)
                    }
                    myCallback.onCallback(messages,0,null)
                }
    }
}