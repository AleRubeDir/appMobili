package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity  : AppCompatActivity() {
    private val RC_SIGN_IN = 9001
    private lateinit var auth : FirebaseAuth
    private lateinit var mGoogleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        auth = FirebaseAuth.getInstance()
        val ggl = findViewById<ImageView>(R.id.ggl)
        val mail = findViewById<ImageView>(R.id.mail)
        ggl.setOnClickListener{
            findViewById<TableLayout>(R.id.selectTab).visibility = View.INVISIBLE
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        mail.setOnClickListener{
            findViewById<TableLayout>(R.id.logTab).visibility = View.VISIBLE
            findViewById<TableLayout>(R.id.selectTab).visibility = View.INVISIBLE
            findViewById<Button>(R.id.signup).setOnClickListener{
                signUpUser()
            }
        }
    }

    private fun selectActivity(mail:String?){
        Log.d("google","----+++---- user email $mail")
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
                .get()
                .addOnSuccessListener { result->
                    for (document in result) {
                        if(document.get("mail").toString() == mail && document.get("type").toString().isNotEmpty()){
                            startActivity(Intent(this, ClienteActivity::class.java))
                        }
                    }
                    val entry = hashMapOf<String, Any?>(
                            "mail" to mail,
                    )
                    db.collection("users").document(mail!!).set(entry)
                            .addOnSuccessListener { Log.d("register","Dati salvati su DB")
                                val intent = Intent(this, FirstTimeActivity::class.java)
                                intent.putExtra("mail", mail)
                                startActivity(intent)}
                            .addOnFailureListener{e-> Log.d("register","Errore salvataggio dati : $e")}
                }
                .addOnFailureListener{ e -> Log.d("google","$e")
                    Toast.makeText(this,"Cannot get",Toast.LENGTH_SHORT).show()}


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("google", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("google", "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("google", "signInWithCredential:success")
                        val user = auth.currentUser
                        Log.d("google","-------- user email ${user!!.email}")
                        selectActivity(user.email)
                        /*
                        Se la chiamata a signInWithCredential riesce, puoi utilizzare il metodo getCurrentUser per ottenere i dati dell'account dell'utente.
                         */
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("google", "signInWithCredential:failure", task.exception)
                    }
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
            return
        }
        val entry = hashMapOf<String, Any?>(
                "mail" to usr.text.toString(),
        )
        val db = FirebaseFirestore.getInstance()
        Log.d("google", "--++++--+++---- user email ${usr.text.toString()}")
        db.collection("users").document(usr.text.toString()).set(entry)
                .addOnSuccessListener { Log.d("register","Dati salvati su DB") }
                .addOnFailureListener{e-> Log.d("register","Errore salvataggio dati : $e")}

        auth.createUserWithEmailAndPassword(usr.text.toString(), pwd.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Utente creato! Esegui l'accesso.",
                                Toast.LENGTH_SHORT).show()
                        val login = Intent(this,LoginActivity::class.java)
                        startActivity(login)
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
