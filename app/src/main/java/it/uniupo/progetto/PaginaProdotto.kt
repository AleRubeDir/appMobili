package it.uniupo.progetto

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.HashMap

class PaginaProdotto  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagina_prodotto)
        val id = intent.getStringExtra("id-prodotto")!!
        var p: Prodotto

        getProdottoFromDB(id.toInt(), object: MyCallback {
            override fun onCallback(p: Prodotto) {
                val tvTitolo = findViewById<TextView>(R.id.titolo)
                tvTitolo.text = p.titolo
                val desc = findViewById<TextView>(R.id.desc)
                desc.text = p.desc
                val prezzo = findViewById<TextView>(R.id.prezzo)
                prezzo.text = p.prezzo
                val np = findViewById<NumberPicker>(R.id.qta)
                np.minValue = 1
                np.maxValue = p.qta
                val img = findViewById<ImageView>(R.id.img)
                Picasso.get().load(p.img).into(img)
                val cart = findViewById<Button>(R.id.cart)
                cart.setOnClickListener{
                    Log.d("pprod","np value ${np.value}")
                    addToCart(p,np.value)
                    Toast.makeText(this@PaginaProdotto,"Aggiunto al carrello",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun addToCart(p : Prodotto, qta : Int) {
        val db = FirebaseFirestore.getInstance()
        var auth = FirebaseAuth.getInstance();
        val user = auth.currentUser!!.email
        val prod: MutableMap<String, Any> = HashMap()
        prod["id"] = p.id
        prod["titolo"] = p.titolo
        prod["prezzo"] = p.prezzo
        prod["qta"] = qta
        var oldqta= 0

        db.collection("carts").document(user!!).collection("products").document(p.id.toString())
                .get()
                .addOnSuccessListener { result->
                    Log.d("qta", "${result.get("qta")} - ${result.get("qta").toString().isNullOrBlank()} / ${result.get("titolo")} / ${result.get("id")} / ${result.get("prezzo")}")
                    if(result.get("qta")!=null) {
                    //if(result.get("qta").toString().isNotBlank()) {
                        oldqta = result.get("qta").toString().toInt()
                        prod["qta"] = oldqta + qta
                    }
                    db.collection("carts").document(user).collection("products").document(p.id.toString())
                            .set(prod)
                            .addOnSuccessListener {
                                Log.d("qta", "Aggiunto prodotto $p con qta $qta totale ${prod["qta"]}")
                            }
                            .addOnFailureListener { e -> Log.w("---", "Errore aggiunta prodotto", e) }
                }



    }

    interface MyCallback{
        fun onCallback(value: Prodotto)
    }

    fun getProdottoFromDB(id: Int,myCallback: MyCallback){
        val db = FirebaseFirestore.getInstance()
        db.collection("products")
            .get()
            .addOnSuccessListener { result->
                for (document in result) {
                    Log.d("pprod", "id vale ${document.get("id").toString()} cerco $id")
                    if (document.get("id").toString() == id.toString()) {
                        var p= Prodotto(document.getLong("id")!!.toInt(), document.get("img")!!.toString(), document.get("titolo").toString(), document.get("desc").toString(), document.get("prezzo").toString(), document.getLong("qta")!!.toInt())
                        Log.d("pprod", "Prodotto vale $p")
                        myCallback.onCallback(p)
                    }

                }
            }
            .addOnFailureListener{ e -> Log.w("---", "Error getting document - GET PRODOTTO FROM DB", e)}
    }

}