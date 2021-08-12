package it.uniupo.progetto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.uniupo.progetto.DatiPersonali.Utente
import it.uniupo.progetto.fragments.MyNewChatRecyclerViewAdapter

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
        var arr = ArrayList<Utente>()
        db.collection("users").get()
                .addOnSuccessListener {
                    for(user in it){
                        if (user.getString("type")=="Rider")
                            arr.add(Utente(user.getString("mail").toString(),user.getString("name").toString(),user.getString("surname").toString(),user.getString("type").toString(),""))
                    }
                    mycallback.onCallback(arr)
                }
    }

    interface MyCallback {
        fun onCallback(riders: ArrayList<Utente>)
    }
}