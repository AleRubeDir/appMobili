package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity  : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        auth = FirebaseAuth.getInstance()
        findViewById<Button>(R.id.signup).setOnClickListener{
            signUpUser()

        }
    }

    private fun signUpUser() {
        val usr = findViewById<EditText>(R.id.usr)
        val pwd = findViewById<EditText>(R.id.pwd)
        if (usr.text.toString().isEmpty()) {
            usr.error = "Inserisci la tua mail"
            usr.requestFocus()
            return
        }

        if (!isEmailValid(usr.text.toString())) {
            usr.error = "Inserisci una mail valida"
            usr.requestFocus()
            return
        }

        if (pwd.text.toString().isEmpty()) {
            pwd.error = "Inserisci la password"
            pwd.requestFocus()
            return
        }
        if(pwd.text.toString().length<8){
            pwd.error = "Lunghezza minima 8 caratteri"
        }

        auth.createUserWithEmailAndPassword(usr.text.toString(), pwd.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Utente creato! Esegui l'accesso.",
                                Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Registrazione fallita, riprova tra poco",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }



}
