package it.uniupo.progetto

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.StoricoOrdini.*
import it.uniupo.progetto.fragments.OrderFragment.*

class RichiamaOrdine : AppCompatActivity() {
    lateinit var rider: String
    lateinit var email: String
    lateinit var recyclerView : RecyclerView

    lateinit var notificationManager : NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.richiama_ordine)
        val tot = findViewById<TextView>(R.id.tot)
        val ordine = findViewById<TextView>(R.id.ordine)
        val lista_prodotti = findViewById<TextView>(R.id.lista_prodotti)
        getOrderCodes(object : MyCallback {
            override fun onCallback(cods: ArrayList<String>) {
                Log.d("richiama","cods -> $cods")
                hasOrderPending(cods,object : MyCallback2 {
                   override fun onCallback(ord: Order) {
                       Log.d("richiama","ord -> $ord")

                       tot.text = getString(R.string.tot,"%.2f".format(ord.tot.toDouble()))
                       ordine.text = getString(R.string.ord,ord.id)

                        var prods = ""
                       for(p in ord.arr){
                           prods += p.titolo + " x " + p.qta + "\n"
                       }
                       lista_prodotti.text = prods
                       val richiama = findViewById<Button>(R.id.richiama)
                       richiama.setOnClickListener{
                           richiamaOrdine(ord)
                         // v    mandaNotifica()
                       }
                    }

                })
            }

        })

    }

/*    private fun mandaNotifica() {
        var pendInt = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        notificationManager =   getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var builder = NotificationCompat.Builder(this)
                .setContentTitle("Consegna in arrivo")
                .setContentText("Hey! Il rider è partito con la tua consegna!")
                .setContentIntent(pendInt)
                .setSmallIcon(R.drawable.rider)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(0, builder)

    }*/

    private fun richiamaOrdine(ord : Order) {
        val db = FirebaseFirestore.getInstance()
        val mail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        for(p in ord.arr) db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("products").document(p.id.toString()).delete()
        db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("details").document("dett").delete()

        db.collection("orders").document(mail).collection("order").document(ord.id.toString()).delete()
                .addOnSuccessListener {
                    Toast.makeText(this,"Ordine annullato",Toast.LENGTH_SHORT).show()
                    //??????????? manda notifica a rider e gestore?
                }
        for(p in ord.arr){
            db.collection("products").document(p.id.toString()).get()
                    .addOnSuccessListener {
                        var newqta = it.getLong("qta")!!.toInt() + p.qta
                        val entry = hashMapOf<String, Any?>(
                                "qta" to newqta,
                        )
                        db.collection("products").document(p.id.toString()).set(entry, SetOptions.merge())
                                .addOnSuccessListener {
                                    startActivity(Intent(this,ClienteActivity::class.java))
                                }
                    }
        }
    }

    private fun getOrderCodes(myCallback: MyCallback){
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
        var cods = arrayListOf<String>()
        Log.d("richiama","email vale $email")

        db.collection("orders").document(email).collection("order").get()
                .addOnSuccessListener {
                    Log.d("richiama", "it = ${it.documents.forEach{it.id}}")
                    for (d in it){
                        Log.d("richiama", "d.id = ${d.id}")
                        cods.add(d.id)
                    }
                    Log.d("richiama","cods vale $cods")
                    myCallback.onCallback(cods)
                }
    }
    interface MyCallback{
        fun onCallback(cods : ArrayList<String>)
    }
    interface MyCallback2{
        fun onCallback(ord : Order)
    }

    private fun hasOrderPending(cods:  ArrayList<String>, myCallback: MyCallback2) {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
        var prod = ArrayList<Prodotto>()
        var ordine : Order
        for(c in cods){
            db.collection("orders").document(email).collection("order").document(c).collection("products").get()
                    .addOnSuccessListener {
                        for ( document in it) {
                            var item = Prodotto(document.getLong("id")!!.toInt(), "img", document.get("titolo").toString(), "desc", document.get("prezzo").toString(), document.getLong("qta")!!.toInt())
                            Log.d("history", "item vale $item")
                            prod.add(item)
                        }
                        var tot = 0.0
                        prod.forEach { tot += it.qta * it.prezzo.toDouble() }
                        Log.d("richiama","tot vale $tot")
                        ordine = Order(c,email,"","",prod,-1,-1,-1,-1,-1,"",tot.toString())
                        myCallback.onCallback(ordine)
                    }

        }
    }
}
