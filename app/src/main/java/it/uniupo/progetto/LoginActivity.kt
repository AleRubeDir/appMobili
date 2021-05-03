package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
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


class LoginActivity : AppCompatActivity() {
    val TAG = "*****"
    private val RC_SIGN_IN = 9001
    private lateinit var auth : FirebaseAuth
    private lateinit var mGoogleSignInClient : GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        auth = FirebaseAuth.getInstance();
        val ggl = findViewById<ImageView>(R.id.ggl)
        val mail = findViewById<ImageView>(R.id.mail)
        ggl.setOnClickListener{
            findViewById<TableLayout>(R.id.selectTab).visibility = View.INVISIBLE
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            // Check for existing Google Sign In account, if the user is already signed in
            // the GoogleSignInAccount will be non-null.
            val account = GoogleSignIn.getLastSignedInAccount(this)
            //updateUI(account)
            signIn()
        }
        mail.setOnClickListener{
            findViewById<TableLayout>(R.id.logTab).visibility = View.VISIBLE
            findViewById<TableLayout>(R.id.selectTab).visibility = View.INVISIBLE
            findViewById<Button>(R.id.signin).setOnClickListener {
                loginUser()
            }
        }

    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
       // updateUI(currentUser)
    }
    private fun signIn() {
        Log.d("login","sei in signIn")
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }


    fun logout(){
        FirebaseAuth.getInstance().signOut();
    }

    private fun loginUser() {
        val usr = findViewById<EditText>(R.id.usr)
        val pwd = findViewById<EditText>(R.id.pwd)

        if (usr.text.toString().isEmpty()) {
            usr.error = "Please enter email"
            usr.requestFocus()
            return
        }

        if (!isEmailValid(usr.text.toString())) {
            usr.error = "Please enter valid email"
            usr.requestFocus()
            return
        }

        if (pwd.text.toString().isEmpty()) {
            pwd.error = "Please enter password"
            pwd.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(usr.text.toString(), pwd.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "User creted. Log in to enter in the restricted area.",
                            Toast.LENGTH_SHORT
                        ).show()
                        selectActivity(usr.text.toString())

                        finish()
                    } else {
                        Toast.makeText(
                            baseContext, "Sign Up failed. Try again after some time.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
    }
    private fun selectActivity(mail:String){
        Log.d("login","mail vale : $mail")
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
                .get()
                .addOnSuccessListener { result->
                    for (document in result) {
                        Log.d("***","${document.id}=> ${document.data} ")
                        Log.d("***","${document.get("mail").toString()} con mail ${mail} e ${document.get("type").toString()} ")
                        if(document.get("mail").toString() == mail && document.get("type").toString().isNotEmpty()){
                            if(document.get("type").toString()=="Cliente"){
                                startActivity(Intent(this, HomeActivity::class.java))
                            }
                            if(document.get("type").toString()=="Rider"){
                                startActivity(Intent(this, RiderActivity::class.java))

                            }
                            if(document.get("type").toString()=="Gestore"){
//                                TODO CUSTOMER
                                startActivity(Intent(this, HomeActivity::class.java))
                            }
                        }else{
                            //utente è al primo accesso, deve scegliere il tipo di account
                            startActivity(Intent(this, FirstTimeActivity::class.java))
                        }
                    }
                }
                .addOnFailureListener{ e -> Log.d("google","$e")}
            Toast.makeText(this,"Cannot get",Toast.LENGTH_SHORT).show()

    }
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        val type = getUserType(user.email)
                        if(type=="Cliente")
                            startActivity(Intent(this, HomeActivity::class.java))
                        else if(type=="Rider")
                            startActivity(Intent(this, RiderActivity::class.java))
                        else if(type=="Gestore")
                            //todo gestore activity
                            startActivity(Intent(this, HomeActivity::class.java))
                        /*
                        Se la chiamata a signInWithCredential riesce, puoi utilizzare il metodo getCurrentUser per ottenere i dati dell'account dell'utente.
                         */
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                      //  updateUI(null)
                    }
                }
    }
    private fun getUserType(user : String) : String{
        Log.d("login","User in getusertype $user")
        val db = FirebaseFirestore.getInstance()
        var t = ""
                db.collection("users").document(user.toString())
                .get()
                .addOnSuccessListener { result ->
                    t = result.getString("type")!!
                    }
        return t
                }
}