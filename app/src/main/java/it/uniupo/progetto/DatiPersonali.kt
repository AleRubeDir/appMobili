package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
                Log.d("prof", "U fuori vale $u")
                val email = findViewById<TextView>(R.id.email)
                val nome = findViewById<TextView>(R.id.nome)
                val cognome = findViewById<TextView>(R.id.cognome)
                val tipo = findViewById<TextView>(R.id.tipo)
                email.text = u.email
                nome.text = u.nome
                cognome.text = u.cognome
                tipo.text = u.tipo
            }
        })
    }
    private fun getUserData(myCallback:MyCallback): Utente {
        var u = Utente("err","err","err","err")
        val fb = FirebaseAuth.getInstance()
        fb.signInWithEmailAndPassword("ale.rube@gmail.com", "123456") //da togliere quando tutto pronto
                .addOnCompleteListener {
                    val user = fb.currentUser!!
                    Log.d("prof", "Utente $user ${user.email} ${user.displayName}  ")

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
                .get()
                .addOnSuccessListener { result->
                    Log.d("prof","$result")
                    for (document in result) {
                        Log.d("prof","${document.id} == ${user.email} ${document.id == user.email}")
                        if(document.id == user.email!!.toString()){
                            //utente ha già scelto il tipo di account
                            u = Utente(document.get("email").toString(),document.get("name").toString(),document.get("surname").toString(),document.get("type").toString())
                            Log.d("prof","$u")
                        }
                        myCallback.onCallback(u)
                    }
                }
                .addOnFailureListener{ e -> Log.w("---","Error getting user info - DatiPersonali",e)}
                }
        return u
    }


    class Utente(val email : String, val nome : String, val cognome : String, val tipo : String){
        override fun toString() :String{
            return "$nome $cognome, email $email. Categoria account $tipo"
        }
    }
}