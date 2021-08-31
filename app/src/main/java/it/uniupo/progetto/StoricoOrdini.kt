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
                if(tipo=="Gestore"){
                    Log.d("history","Gestore")
                    gettAllUsersWithOrders(object : MyCallback2{
                        override fun onCallback(users: ArrayList<String>) {

                            for(u in users) {
                                Log.d("history","$u")
                                getAllCodes(u,object : MyCallback2 {
                                    override fun onCallback(cods: ArrayList<String>) {
                                        Log.d("history","$cods")
                                        getOrdersByUser(u,object : MyCallback {
                                            override fun onCallback(ord : ArrayList<Order>){
                                                Log.d("history", "Ord vale ${ord}")
                                                recyclerView.adapter = MyHistoryOrderAdapter(ord)
                                            }
                                        }, cods)

                                    }
                                })
                            }
                        }
                    })

                }else if(tipo=="Cliente"){
                    val mail = FirebaseAuth.getInstance().currentUser!!.email.toString()
                    getAllCodes(mail, object : MyCallback2{
                        override fun onCallback(cods: ArrayList<String>) {
                            Log.d("history","cods vale : ${cods}")
                            getOrdersByUser(mail,object : MyCallback{
                                override fun onCallback(ord : ArrayList<Order>){
                                   // Log.d("history","Ord vale ${ord.arr}")
                                    recyclerView.adapter = MyHistoryOrderAdapter(ord)
                                }
                            },cods)

                        }
                    })


                }
            }
        })





    }

    private fun getUserType(mycallback: MyCallback3){
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val db = FirebaseFirestore.getInstance()
        var t = "null"
        db.collection("users").document(user!!)
                .get()
                .addOnSuccessListener { result ->
                    if(!result.getString("type").isNullOrBlank())
                        t = result.getString("type")!!
                     mycallback.onCallback(t)
                }
    }

    private fun gettAllUsersWithOrders(mycallback: MyCallback2){
        var users = arrayListOf<String>()
        val db = FirebaseFirestore.getInstance()

        db.collection("orders_history").get()
                .addOnSuccessListener {
                    for(doc in it){
                        users.add(doc.id)
                    }
                    mycallback.onCallback(users)
                }
    }
    private fun getAllCodes(mail : String , mycallback: MyCallback2){

        val db = FirebaseFirestore.getInstance()
        var cods = arrayListOf<String>()
        var prod = arrayListOf<Prodotto>()
        db.collection("orders_history").document(mail).collection("orders").get()
                .addOnSuccessListener { doc ->
                    for (d in doc) {
                        cods.add(d.id)
                    }
                    mycallback.onCallback(cods)
                }
    }
    private fun getOrdersByUser(mail : String , mycallback : MyCallback, cods : ArrayList<String>) {
        val db = FirebaseFirestore.getInstance()
        var prod = arrayListOf<Prodotto>()
        var ord : Order
        val ords = arrayListOf<Order>()
        for(c in cods)
        {
            Log.d("history","c vale $c")
            db.collection("orders_history").document(mail).collection("orders").document(c).collection("products").get()
                    .addOnSuccessListener {
                        Log.d("history", "dentro listener")
                        for (document in it) {
                            Log.d("history", "dentro for")
                            var item = Prodotto(document.getLong("id")!!.toInt(), "img", document.get("titolo").toString(), "desc", document.get("prezzo").toString(), document.getLong("qta")!!.toInt())
                            Log.d("history", "item vale $item")
                            prod.add(item)
                        }
                    }
            db.collection("orders_history").document(mail).collection("orders").document(c).collection("others").get()
                    .addOnSuccessListener {
                        Log.d("history","dentro secondo listener")
                        var tipo =""
                        var data = ""
                        var ratingQ = -1
                        var ratingV = -1
                        var ratingC = -1
                        var rider = ""
                        var id = ""
                        for(d in it){
                            id = c
                            rider = d.getString("rider").toString()
                            tipo = d.getString("tipo").toString()
                            data = convertLongToTime(d.getTimestamp("data")!!.seconds)
                            ratingQ = d.getLong("ratingQ")!!.toInt()
                            ratingV = d.getLong("ratingV")!!.toInt()
                            ratingC = d.getLong("ratingC")!!.toInt()

                        }
                        var tot = 0.0
                        prod.forEach{
                            tot += ( it.qta * it.prezzo.toDouble() )
                        }
                        ord = Order(id,mail, rider , tipo, prod, ratingQ,ratingV,ratingC, data, tot.toString() )
                        Log.d("history","dentro ord vale $ord")
                        ords.add(ord)

                    }
            mycallback.onCallback(ords)

        }

    }
    fun convertLongToTime(time: Long): String {
        val date = Date(time*1000)
        //  Log.d("mess","time vale $time date vale $date")
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }
    interface MyCallback {
        fun onCallback(ord: ArrayList<Order>)
    }

    interface MyCallback2 {
        fun onCallback(cods: ArrayList<String>)
    }
    interface MyCallback3 {
        fun onCallback(tipo : String)
    }
       /* class Order(var id : String?,var cliente : String, var rider : String , var tipo : String, var arr : ArrayList<Prodotto>, var ratingQ : Int = -1, var ratingV : Int = -1, var ratingC : Int = -1, var date : String, var tot : String , var richiamato : Int = 0) {
        override fun toString(): String {
            return "Tipo : $tipo \n ratingQ : $ratingQ \n ratingV : $ratingV \n ratingC : $ratingC \n date : $date \n arr : $arr "
        }
    }*/
}


