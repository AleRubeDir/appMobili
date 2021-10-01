package it.uniupo.progetto

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.uniupo.progetto.DatiPersonali.Utente
import it.uniupo.progetto.recyclerViewAdapter.*

class NewChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_chat)

        val recyclerView = findViewById<RecyclerView>(R.id.contacts)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        getRiders(object: MyCallback{
            override fun onCallback(riders: ArrayList<Utente>) {
                recyclerView.adapter = MyNewChatRecyclerViewAdapter(riders)
            }
        })

        }

    private fun getRiders(mycallback: MyCallback) {
        val db = FirebaseFirestore.getInstance()
        val arr = ArrayList<Utente>()
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()

        db.collection("users").get()
                .addOnSuccessListener {
                    for(user in it){
                        if (user.getString("type")=="Rider")
                            arr.add(Utente(user.getString("mail").toString(),user.getString("name").toString(),user.getString("surname").toString(),user.getString("type").toString(),""))
                    }
                    db.collection("chats").document(user).collection("contacts").get()
                            .addOnSuccessListener {
                                for(user in it){
                                    arr.remove(arr.find{it.email == user.getString("mail").toString()})
                                }
                                Log.d("riderToChat","arr vale $arr")
                                mycallback.onCallback(arr)
                            }
                }
    }

    interface MyCallback {
        fun onCallback(riders: ArrayList<Utente>)
    }
}