package it.uniupo.progetto

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
        val order = findViewById<CardView>(R.id.order)
        val prodotti = findViewById<TextView>(R.id.prodotti)
        order.visibility = View.INVISIBLE
        prodotti.visibility = View.INVISIBLE 
        tot.text = ""
        ordine.text = ""
        lista_prodotti.text = ""
             hasOrderPending(object : MyCallback2 {
                   override fun onCallback(ord: Order) {
                       if(ordine!=null) {

                           Log.d("richiama", "ord -> $ord")
                           tot.text = getString(R.string.tot, "%.2f".format(ord.tot.toDouble()))
                           ordine.text = getString(R.string.ord, ord.id)
                           order.visibility = View.VISIBLE
                           prodotti.visibility = View.VISIBLE
                           var prods = ""
                           for (p in ord.arr) prods += p.titolo + " x " + p.qta + "\n"
                           lista_prodotti.text = prods
                           val richiama = findViewById<Button>(R.id.richiama)

                           richiama.setOnClickListener {
                               richiamaOrdine(ord)
                           }

                       }
                    }

                })

    }

    private fun richiamaOrdine(ord : Order) {
        val db = FirebaseFirestore.getInstance()
        val richiamato = hashMapOf<String,Any>(
                "richiamato" to true
        )
        val mail = FirebaseAuth.getInstance().currentUser!!.email.toString()
//        for(p in ord.arr) db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("products").document(p.id.toString()).delete()
//        db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("details").document("dett").delete()
       Log.d("richiama2","ordrider vale ${ord.rider}")
        rider = ord.rider
        Log.d("richiamato", "order id: $rider")

        if(rider!=""){
            Log.d("richiamato","dentro if")
                db.collection("delivery").document(ord.rider).get()
                    .addOnSuccessListener {
                        db.collection("delivery").document(ord.rider).set(richiamato, SetOptions.merge())
                    }
            db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("details").document("dett").delete()
            db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("products").get()
                    .addOnSuccessListener {
                        for (d in it) {
                            db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("products").document(d.id).delete()
                        }
                    }

            db.collection("orders").document(mail).delete()
            db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("rider").document("r").delete()
            db.collection("orders").document(mail).collection("order").document(ord.id.toString()).delete()
                db.collection("client-rider").document(mail).collection("rider").document(rider).delete()
                db.collection("client-rider").document(mail).delete()

            db.collection("assignedOrders").document(ord.id.toString()).delete()

            db.collection("delivery").document(rider).collection("orders").document(ord.id.toString()).delete()
            db.collection("delivery").document(rider).delete()

        } else {

            db.collection("orders").document(mail).collection("order").document(ord.id.toString()).delete()
                    .addOnSuccessListener {
                        RiderActivity.ordId = ""
                        Toast.makeText(this,"Ordine annullato",Toast.LENGTH_SHORT).show()
                    }

            Log.d("richiamato", "email id:$mail")
            db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("details").document("dett").delete()
            db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("products").get()
                    .addOnSuccessListener {
                        for (d in it) {
                            db.collection("orders").document(mail).collection("order").document(ord.id.toString()).collection("products").document(d.id).delete()
                        }
                    }

            db.collection("orders").document(mail).delete()
            Log.d("richiamato","ordine id odpo ccancellazione ${ord.id.toString()}")
            db.collection("toassignOrders").document(ord.id.toString()).delete()
            for(p in ord.arr){
                db.collection("products").document(p.id.toString()).get()
                        .addOnSuccessListener {
                            var newqta = it.getLong("qta")!!.toInt() + p.qta
                            val entry = hashMapOf<String, Any?>(
                                    "qta" to newqta ,
                            )
                            db.collection("products").document(p.id.toString()).set(entry, SetOptions.merge())

                        }
            }

//            Log.d("richiamato", "ordine id:" + RiderActivity.ordId!!)

        }
        startActivity(Intent(this , ClienteActivity::class.java))


    }


    interface MyCallback{
        fun onCallback(cods : ArrayList<String>)
    }
    interface MyCallback2{
        fun onCallback(ord : Order)
    }

    private fun hasOrderPending(myCallback: MyCallback2) {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email.toString()

        var ordine : Order
        var ordineCod  = ""

            val prod = ArrayList<Prodotto>()
//            controllare se tra tutti i delivery c'è n'è qualcuno appartenente al cliente

        db.collection("orders").document(email).collection("order").get()
                .addOnSuccessListener {
                    for(d in it){
                        ordineCod = d.id
                    }
                var rider = ""
                db.collection("orders").document(email).collection("order").document(ordineCod).collection("rider").document("r").get()
                        .addOnSuccessListener {

                            if(it.getString("mail") != null ){
                                rider = it.getString("mail").toString()
                            }
                            db.collection("orders").document(email).collection("order").document(ordineCod).collection("products").get()
                                    .addOnSuccessListener {
                                        for ( document in it) {
                                                 var item = Prodotto(document.getLong("id")!!.toInt(), "img", document.get("titolo").toString(), "desc", document.get("prezzo").toString(), document.getLong("qta")!!.toInt())
                                                 Log.d("history", "item vale $item")
                                                 prod.add(item)
                                        }
                            var tot = 0.0
                            prod.forEach { tot += it.qta * it.prezzo.toDouble() }
                            Log.d("richiama","tot vale $tot")
                            ordine = Order(ordineCod,email,rider,"",prod,-1,-1,-1,-1,-1,"",tot.toString(),0,0)
                            myCallback.onCallback(ordine)
                        }
                        }
                    }

    }
}
