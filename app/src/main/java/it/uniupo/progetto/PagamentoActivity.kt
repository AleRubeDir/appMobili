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

        var ord_id = intent.getStringExtra("ord_id")!!
        val btn = findViewById<Button>(R.id.btn)
        val rg = findViewById<RadioGroup>(R.id.group)
        val tot = findViewById<TextView>(R.id.tot)
        val ind = findViewById<TextView>(R.id.indirizzo)

        getIndirizzo(object : MyCallback {
            override fun onCallback(ris :String) {
                Log.d("indirizzo", ris)
                var arr = ris.split(",")
                ind.text = arr[0] + " " + arr[1] + ","+ arr[2] + " "
            }
        },(FirebaseAuth.getInstance().currentUser?.email))
        var totdoub = "%.2f".format(ClienteActivity.tot)
        tot.text = getString(R.string.cash,totdoub)
        btn.setOnClickListener{
            if(rg.checkedRadioButtonId==-1) Toast.makeText(this,"Seleziona un metodo di pagamento", Toast.LENGTH_LONG).show()
            else {
                var i = 1
                if(rg.checkedRadioButtonId==R.id.carta) i =0
            selezionaMetodo(ord_id,i)
            showAlert()
            }
        }

    }

    private fun getIndirizzo(myCallback: MyCallback,email: String?) {
        val db = FirebaseFirestore.getInstance()
        var add=""
        db.collection("users").document(email!!).get()
                .addOnSuccessListener { document->
                    add = document.getString("address").toString()
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


                    for(p in ClienteActivity.carrello) diminuisciQtaDB(p)
                    svuotaCarrello()
                   /* //??????????????????? controllare se funziona
                    notificationManager =   getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    var pendInt = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                    var builder = NotificationCompat.Builder(this)
                            .setContentTitle("Consegna in arrivo")
                            .setContentText("Seleziona il rider per questa consegna")
                            .setContentIntent(pendInt)
                            .setSmallIcon(R.drawable.cart)
                            .setAutoCancel(true)
                            .build()
                    notificationManager.notify(0, builder)*/
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

    private fun diminuisciQtaDB(p: Prodotto) {
        val db = FirebaseFirestore.getInstance()
        db.collection("products").document(p.id.toString()).get()
                .addOnSuccessListener { document->
                    val vecchiaqta = document.getLong("qta")!!.toInt()
                    val nuovaqta = vecchiaqta-p.qta
                    val entry = hashMapOf<String, Any?>(
                            "qta" to nuovaqta,
                    )
                    db.collection("products").document(p.id.toString()).set(entry, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d("qta","Qta prodotto aggiornata con successo")
                            }
                            .addOnFailureListener{
                                Log.w("qta","Errore modifica qtaDB $it")
                                it.printStackTrace()
                            }

                }
                .addOnFailureListener{
                    Log.w("qta","Errore ottenimento qtaDB $it")
                    it.printStackTrace()
                }
    }

    fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..8)
                .map { allowedChars.random() }
                .joinToString("")
    }
    private fun selezionaMetodo(ord_id : String, i: Int) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser?.email.toString()
        var tipo = ""
        if(i==0) tipo = "Carta"
        else tipo="Contanti"
        val entry = hashMapOf<String,Any>(
            "id" to i,
            "tipo" to tipo
        )
        val dummy = hashMapOf<String,Any>(
                " " to " "
        )
        db.collection("toassignOrders").document(ord_id).set(entry)
        db.collection("orders").document(user).collection("order").document(ord_id).collection("details").document("dett").set(entry)
            .addOnSuccessListener { document->
                Log.d("myscelta","Selezionato metodo di pagamento")
            }
                .addOnFailureListener{e->
                    Log.w("pagamento","Errore scelta metodo di pagamento $e")
                    e.printStackTrace()
                }
    }
}