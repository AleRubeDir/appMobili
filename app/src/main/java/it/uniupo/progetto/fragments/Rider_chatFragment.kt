package it.uniupo.progetto.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.*
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Rider_chatFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_rider_list, container, false)

        val chatClient = view.findViewById<RelativeLayout>(R.id.chat_rider_cliente)
        val chatGestore = view.findViewById<RelativeLayout>(R.id.chat_rider_gestore)

        if(RiderActivity.flag_consegna==1){
            chatClient.visibility = View.VISIBLE
        getChats(object : ChatGestoreFragment.MyCallbackMessages {
                override fun onCallback(value: ArrayList<Messaggio>, contatto: Contatto?) {
                    Log.d("chatRider", "fuori $value ")
                    val chatUtente = Chat(contatto!!,value)
                    val name = view.findViewById<TextView>(R.id.name)
                    val surname = view.findViewById<TextView>(R.id.surname)
                    val mail = view.findViewById<TextView>(R.id.mail)
                    val ora = view.findViewById<TextView>(R.id.ora)
                    val data = view.findViewById<TextView>(R.id.data)
                    val anteprima = view.findViewById<TextView>(R.id.anteprima)

                    name.text = chatUtente.contatto.nome
                    surname.text = chatUtente.contatto.cognome
                    mail.text = chatUtente.contatto.mail
                    if(value.size > 0 ){
                    Log.d("chatRider", "value> 0 valore $value ")
                    val vora = value.last().ora.toDate().hours.toString()
                    var vminuti = value.last().ora.toDate().minutes.toString()
                    if(vminuti.length==1) vminuti = "0"+  value.last().ora.toDate().minutes.toString()
                    ora.text = view.context.getString(R.string.orario,vora,vminuti)

                    data.text = convertLongToTime(value.last().ora.seconds)

                    anteprima.text = value.last().testo
                    }

                }
             })
        }
        getGestoreChats(object : ChatGestoreFragment.MyCallbackMessages {
            override fun onCallback(value: ArrayList<Messaggio>, contatto: Contatto?) {
                Log.d("chatRider","dentro getGestoreChats $value, $contatto")
                val chatUtente = Chat(contatto!!,value)
                val name = view.findViewById<TextView>(R.id.name_gestore)
                val surname = view.findViewById<TextView>(R.id.surname_gestore)
                val mail = view.findViewById<TextView>(R.id.mail_gestore)
                val ora = view.findViewById<TextView>(R.id.ora_gestore)
                val data = view.findViewById<TextView>(R.id.data_gestore)
                val anteprima = view.findViewById<TextView>(R.id.anteprima_gestore)

                name.text = chatUtente.contatto.nome
                surname.text = chatUtente.contatto.cognome
                mail.text = chatUtente.contatto.mail
                if(value.size > 0 ){
                Log.d("chatRider", "valore " + (value.size) + value)

                    val vora = value.last().ora.toDate().hours.toString()
                    var vminuti = value.last().ora.toDate().minutes.toString()
                    if(vminuti.length==1) vminuti = "0"+  value.last().ora.toDate().minutes.toString()
                    ora.text = view.context.getString(R.string.orario,vora,vminuti)

                data.text = convertLongToTime(value.last().ora.seconds)
                    value.sortBy { it.ora.seconds }
                    Log.d("chatRider","terza vale $value")
                anteprima.text = value.last().testo
                }

            }

        })
        val mail = view.findViewById<TextView>(R.id.mail)

        chatClient.setOnClickListener{
            val intent = Intent(view.context,ChatActivity::class.java)
            intent.putExtra("mail",mail.text.toString())
            intent.putExtra("fromRider", true)
            view.context.startActivity(intent)
        }

        chatGestore.setOnClickListener{
            val intent = Intent(view.context,ChatActivity::class.java)
            intent.putExtra("mail","gestore@gmail.com")
            intent.putExtra("fromRider", true)
            view.context.startActivity(intent)
        }
        return view
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time*1000)
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }

    private fun getChats(myCallback: ChatGestoreFragment.MyCallbackMessages) {
        val db = FirebaseFirestore.getInstance()
        val usr = FirebaseAuth.getInstance().currentUser!!.email.toString()
        Log.d("chatRider", "dentro getChats")
        db.collection("chats").document(usr).collection("contacts").get()
                .addOnCompleteListener {
                    for (d in it.result) {
                        if (d.getString("tipo") == "Cliente") {
                            val clientMail = d.id
                            Log.d("chatRider", "clientMail vale $clientMail")
                            db.collection("chats").document(clientMail).collection("contacts").document(usr).collection("messages").get()
                                    .addOnSuccessListener { result ->
                                        var messages = arrayListOf<Messaggio>()
                                        for (document in result) {
                                            val mess = Messaggio(document.getLong("inviato")!!.toInt(), document.get("ora") as Timestamp, document.get("testo").toString())
                                            messages.add(mess)
                                        }
                                        var surname = ""
                                        var name = ""
                                        var tipo = ""
                                        db.collection("chats").document(usr).collection("contacts").document(clientMail).get().addOnSuccessListener {
                                            //controllo nel caso in cui la chat non contenga messaggi
                                            name = it.getString("name")!!
                                            surname = it.getString("surname")!!
                                            val contatto = Contatto(clientMail, name, surname)
                                            myCallback.onCallback(messages, contatto)
                                        }
                                    }
                        }
                    }
                }
    }

    private fun getGestoreChats(myCallback: ChatGestoreFragment.MyCallbackMessages) {
        val db = FirebaseFirestore.getInstance()
        val usr = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val gestoreMail = "gestore@gmail.com"

        // chat gestore - rider
        //prendo messaggi in chats--> gestore--> contacts --> rider --> messages

        db.collection("chats").document(gestoreMail).collection("contacts").document(usr).collection("messages").get()
                .addOnCompleteListener { res ->
                                    val messages = arrayListOf<Messaggio>()
                                    for (document in res.result) {
                                        val mess = Messaggio(document.getLong("inviato")!!.toInt(), document.get("ora") as Timestamp, document.get("testo").toString())
                                        messages.add(mess)
                                    }
                                    var surname = ""
                                    var name = ""
                                    var tipo = ""
                            db.collection("chats").document(gestoreMail).get()
                                    .addOnSuccessListener {
                                        name = it.getString("name")!!
                                        surname = it.getString("surname")!!
                                        tipo = it.getString("tipo")!!
                                        val contatto = Contatto(gestoreMail, name, surname)
                                        myCallback.onCallback(messages, contatto)
                                    }
                            }
                    }
                }
