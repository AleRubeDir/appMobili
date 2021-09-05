package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.concurrent.thread

class DatiPersonali  : AppCompatActivity() {
    interface MyCallback{
        fun onCallback(u: Utente)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dati_personali)
        getUserData(object: MyCallback {
            override fun onCallback(u: Utente) {
                val email = findViewById<TextView>(R.id.email)
                val nome = findViewById<TextView>(R.id.nome)
                val cognome = findViewById<TextView>(R.id.cognome)
                val tipo = findViewById<TextView>(R.id.tipo)
                val indirizzo = findViewById<TextView>(R.id.indirizzo)
                email.text = u.email
                nome.text = u.nome
                cognome.text = u.cognome
                tipo.text = u.tipo
                if(u.indirizzo!="null") {
                    indirizzo.visibility = View.VISIBLE
                    indirizzo.text = u.indirizzo
                }
            }
        })
    }
    private fun getUserData(myCallback:MyCallback){
        var u = Utente("err","err","err","err","null")
        val fb = FirebaseAuth.getInstance()
        val user = fb.currentUser
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
                .get()
                .addOnSuccessListener { result->
                    Log.d("prof","$result")
                    for (document in result) {
                        if(document.id == user?.email!!.toString()){
                            //utente ha già scelto il tipo di account
                            u = Utente(document.get("mail").toString(),document.get("name").toString(),document.get("surname").toString(),document.get("type").toString(),document.get("address").toString())
                            Log.d("prof","$u")
                        }
                        myCallback.onCallback(u)
                    }
                }
                .addOnFailureListener{ e -> Log.w("---","Error getting user info - DatiPersonali",e)}
    }



    class Utente(val email : String, val nome : String, val cognome : String, val tipo : String, val indirizzo : String ){
        override fun toString() :String{
            return "$nome $cognome, email $email. Categoria account $tipo. Indirizzo : $indirizzo"
        }
    }
}