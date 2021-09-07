package it.uniupo.progetto

import android.app.NotificationManager
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class PagamentoActivity : AppCompatActivity() {

    lateinit var notificationManager : NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagamento)

        val ordId = intent.getStringExtra("ord_id")!!
        val btn = findViewById<Button>(R.id.btn)
        val rg = findViewById<RadioGroup>(R.id.group)
        val tot = findViewById<TextView>(R.id.tot)
        val ind = findViewById<TextView>(R.id.indirizzo)
        val cliente = FirebaseAuth.getInstance().currentUser?.email
        getIndirizzo(object : MyCallback {
            override fun onCallback(ris :String) {
                Log.d("indirizzo", "indirizzo vale $ris")
                val arr = ris.split(",")
                val indirizzo = arr[0] + " " + arr[1] + ","+ arr[2] + " "
                Log.d("indirizzo", "indirizzo splittato vale $indirizzo")
                ind.text = indirizzo
                btn.setOnClickListener{
                    if(rg.checkedRadioButtonId==-1) Toast.makeText(this@PagamentoActivity,"Seleziona un metodo di pagamento", Toast.LENGTH_LONG).show()
                    else {
                        var i = 1
                        if(rg.checkedRadioButtonId==R.id.carta) i =0
                        selezionaMetodo(cliente!!,indirizzo, ordId,i)
                        showAlert()
                    }
                }
            }
        },cliente )
        var totdoub = "%.2f".format(ClienteActivity.tot)
        tot.text = getString(R.string.cash,totdoub)


    }

    private fun getIndirizzo(myCallback: MyCallback,email: String?) {
        val db = FirebaseFirestore.getInstance()
        var add : String
        db.collection("users").document(email!!).get()
                .addOnSuccessListener { document->
                    add = document.getString("address").toString()
                    Log.d("indirizzo","dentro add vale $add")
                    myCallback.onCallback(add)
                }

    }
    interface MyCallback{
        fun onCallback(ris: String)
    }
    private fun showAlert() {
        AlertDialog.Builder(this)
                .setTitle("Ordine inviato")
                .setMessage("Riceverai una notifica appena il rider partirà con il tuo ordine")
                .setNeutralButton("Chiudi")
                { _: DialogInterface, _: Int ->
                    svuotaCarrello()
                    startActivity(Intent(this,ClienteActivity::class.java))

                }
                .show()

    }

    private fun svuotaCarrello() {

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser?.email.toString()
        Log.d("carts","User = $user")
        for(p in ClienteActivity.carrello) {
            db.collection("carts").document(user).collection("products").document(p.id.toString()).delete()
                    .addOnSuccessListener {
                        Log.d("cart", "Prodotto rimosso dal carrello")
                    }
                    .addOnFailureListener {
                        Log.w("cart", "Errore clear carrello  $it")
                        it.printStackTrace()
                    }
            db.collection("carts").document(user).get()
                    .addOnSuccessListener { document ->
                        Log.d("carts", document.toString())
                    }
        }
        ClienteActivity.carrello.clear()
        ClienteActivity.tot=0.0
    }

    private fun selezionaMetodo(cliente : String ,indirizzo : String , ordId : String, i: Int) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser?.email.toString()
        var tipo : String
        if(i==0) tipo = "Carta"
        else tipo="Contanti"
        val entry = hashMapOf<String,Any>(
            "tipo" to tipo,
            //indirizzo ordine
            "indirizzo" to indirizzo,
            "cliente" to cliente
        )
        db.collection("toassignOrders").document(ordId).set(entry)
        db.collection("orders").document(user).collection("order").document(ordId).collection("details").document("dett").set(entry)
            .addOnSuccessListener {
                Log.d("myscelta","Selezionato metodo di pagamento")
            }
                .addOnFailureListener{e->
                    Log.w("pagamento","Errore scelta metodo di pagamento $e")
                    e.printStackTrace()
                }
    }
}