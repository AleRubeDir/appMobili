package it.uniupo.progetto.recyclerViewAdapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.ProfileFragment.Azione
import it.uniupo.progetto.*
import it.uniupo.progetto.fragments.*

class MyOrderActionsAdapter(private val array: ArrayList<Azione>) : RecyclerView.Adapter<MyOrderActionsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.action, parent, false)

        val id  = view.findViewById<TextView>(R.id.id)
        val row = view.findViewById<RelativeLayout>(R.id.action_row)
        val nome  = view.findViewById<TextView>(R.id.nome)

        row.setOnClickListener{
            Log.d("prof","Cliccato ${id.text} - ${nome.text} ")
            if(id.text=="0"){
                // Traccia il tuo ordine
                val intent = Intent(view.context, RiderPosition::class.java)
                /*intent.putExtra("id-prodotto", id.text )*/
                view.context.startActivity(intent)
            }
            if(id.text=="1"){
                //Chat con rider
                    val mail = FirebaseAuth.getInstance().currentUser!!.email.toString()
                    getRiderForUser(mail,object : MyCallback{
                        override fun onCallback(rider: String) {
                            val intent = Intent(view.context, ChatActivity::class.java)
                            intent.putExtra("mail", rider )
                            view.context.startActivity(intent)
                        }

                    })

            }
            if(id.text=="2"){
                //Richiama ordine
                val intent = Intent(view.context, RichiamaOrdine::class.java)
                /*intent.putExtra("id-prodotto", id.text )*/
                view.context.startActivity(intent)
            }
            if(id.text=="3"){
                //Storico degli ordini
                val intent = Intent(view.context, StoricoOrdini::class.java)
                view.context.startActivity(intent)
                /*parent.context.getSharedPreferences("login",0).edit().remove("login").apply()
                FirebaseAuth.getInstance().signOut()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(view.context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                val googleSignInClient = GoogleSignIn.getClient(parent.context, gso)
                googleSignInClient.signOut()
                view.context.startActivity(Intent(view.context, MainActivity::class.java))
                Toast.makeText(view.context,"Logout effettuato", Toast.LENGTH_SHORT).show()*/
            }
        }
        return ViewHolder(view)
    }

    private fun getRiderForUser(mail: String, myCallback: MyCallback) {
        val db = FirebaseFirestore.getInstance()
        Log.d("chat","$mail")
        db.collection("client-rider").document(mail).collection("rider").get()
            .addOnSuccessListener {
                for(d in it)    myCallback.onCallback(d.id)
            }
    }

    interface MyCallback {
        fun onCallback(rider: String)
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var text: TextView = view.findViewById(R.id.nome)
        var id : TextView = view.findViewById(R.id.id)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = array[position]
        holder.text.text = item.nome
        holder.id.text = item.id.toString()
    }
    override fun getItemCount(): Int = array.size



}