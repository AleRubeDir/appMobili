package it.uniupo.progetto.fragments

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R
import it.uniupo.progetto.StoricoOrdini.*
import it.uniupo.progetto.recyclerViewAdapter.*
class RichiamaOrdine : AppCompatActivity() {
    lateinit var rider: String
    lateinit var email: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.richiama_ordine)
        getOrderCodes(object : MyCallback{
            override fun onCallback(cods: ArrayList<String>) {
                hasOrderPending(cods,object : MyCallback2{
                   override fun onCallback(ord: Order) {


                    }

                })
            }

        })
        val richiama = findViewById<Button>(R.id.richiama)
        richiama.setOnClickListener{

        }
    }

    private fun getOrderCodes(myCallback: MyCallback){
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
        var cods = arrayListOf<String>()
        db.collection("orders").document(email).collection("order").get()
                .addOnSuccessListener {
                    for (d in it){
                        cods.add(d.id)
                    }
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
                        ordine = Order(email,"","",prod,0,"",tot.toString())
                        myCallback.onCallback(ordine)
                    }

        }
    }
}
