package it.uniupo.progetto

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import it.uniupo.progetto.recyclerViewAdapter.*
import it.uniupo.progetto.fragments.OrderFragment.*

class StoricoOrdini : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_order_history)
        val recyclerView = findViewById<RecyclerView>(R.id.profile_actions)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        getUserType(object : MyCallback3{
            override fun onCallback(tipo: String) {
                getOrdersByUser(object : MyCallback{
                    override fun onCallback(ord : ArrayList<Order>){
                            recyclerView.adapter = MyHistoryOrderAdapter(ord,tipo)
                    }
                },tipo)
            }
        })
    }


    private fun getUserType(mycallback: MyCallback3){
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val db = FirebaseFirestore.getInstance()
        var t = "null"
        db.collection("users").document(user)
                .get()
                .addOnSuccessListener { result ->
                    if(!result.getString("type").isNullOrBlank())
                        t = result.getString("type")!!
                     mycallback.onCallback(t)
                }
    }

    private fun getOrdersByUser(mycallback : MyCallback,tipo :String ) {
        val db = FirebaseFirestore.getInstance()
        var prod = arrayListOf<Prodotto>()
        var ord : Order
        val ords = arrayListOf<Order>()
        var currUser = FirebaseAuth.getInstance().currentUser!!.email.toString() //gestore
        var toBeChecked = "mail"

            db.collection("orders_history").get()
                    .addOnCompleteListener {
                            for(d in it.result) {
                                if(tipo=="Rider") toBeChecked = "rider"
                                if (d.getString(toBeChecked).toString() == currUser) {
                                    val id = d.id
                                    val cliente = d.getString("mail").toString()
                                    Log.d("history3","cliente vale $cliente")
                                    val rider = d.getString("rider").toString()
                                    val tipo = d.getString("tipoPagamento").toString()
                                    val data = convertLongToTime(d.getTimestamp("data")?.seconds)
                                    val ratingQ = d.getLong("ratingQ")?.toInt()
                                    val ratingV = d.getLong("ratingV")?.toInt()
                                    val ratingC = d.getLong("ratingC")?.toInt()
                                    val risultatoOrdine = d.getLong("risultatoOrdine")?.toInt()
                                    ord = Order(id, cliente, rider, tipo, prod, ratingQ, ratingV, ratingC, -1, -1, data, "0", 0, risultatoOrdine)
                                    Log.d("history2", "dentro ord vale $ord")
                                    ords.add(ord)
                                }
                            }
                        Log.d("history2", "dentro ords vale $ords")
            mycallback.onCallback(ords)
                    }
    }
    fun convertLongToTime(time: Long?): String {
        if(time!=null){
        val date = Date(time*1000)
        //  Log.d("mess","time vale $time date vale $date")
        val format = SimpleDateFormat("dd/MM/yyyy",Locale.ITALY)
        return format.format(date)
        }
        return "err"
    }
    interface MyCallback {
        fun onCallback(ord: ArrayList<Order>)
    }

    interface MyCallback3 {
        fun onCallback(tipo : String)
    }

}


