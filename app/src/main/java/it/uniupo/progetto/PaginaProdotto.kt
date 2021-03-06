package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.HashMap

class PaginaProdotto  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagina_prodotto)
        val id = intent.getStringExtra("id-prodotto")!!
        getProdottoFromDB(id.toInt(), object: MyCallback {
            override fun onCallback(value: Prodotto) {
                val tvTitolo = findViewById<TextView>(R.id.titolo)
                tvTitolo.text = value.titolo
                val desc = findViewById<TextView>(R.id.desc)
                desc.text = value.desc
                val prezzo = findViewById<TextView>(R.id.prezzo)
                prezzo.text = getString(R.string.cash,value.prezzo)
                val np = findViewById<com.shawnlin.numberpicker.NumberPicker>(R.id.number_picker)
                np.minValue = 1
                np.maxValue = value.qta
                val img = findViewById<ImageView>(R.id.img)
                Picasso.get().load(value.img).into(img)
                val cart = findViewById<Button>(R.id.cart)
                cart.setOnClickListener{
                    Log.d("pprod","np value ${np.value}")
                    addToCart(value,np.value)
                    val home = Intent(applicationContext,ClienteActivity::class.java)
                    home.putExtra("cart","vai")
                    startActivity(home)
                }
            }
        })
    }

    fun addToCart(p : Prodotto, qta : Int) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance();
        val user = auth.currentUser!!.email
        val prod: MutableMap<String, Any> = HashMap()
        prod["id"] = p.id
        prod["titolo"] = p.titolo
        prod["prezzo"] = p.prezzo
        prod["qta"] = qta
        var oldqta= 0
        Log.d("qta","p.id = ${p.id}")
        db.collection("products").document(p.id.toString()).get()
                .addOnSuccessListener {
                    Log.d("qta", "$it")
                    val max = it.getLong("qta")!!.toInt()
                    db.collection("carts").document(user!!).collection("products").document(p.id.toString())
                            .get()
                            .addOnSuccessListener { result ->
                                Log.d("qta", "$result \n ${result.get("qta")} - ${result.get("qta").toString().isBlank()} / ${result.get("titolo")} / ${result.get("id")} / ${result.get("prezzo")}")
                                if (result.get("qta") != null) {
                                    oldqta = result.get("qta").toString().toInt()
                                    if (oldqta + qta > max) {
                                        Toast.makeText(
                                            this@PaginaProdotto,
                                            "Quantit?? massima raggiunta",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                    else  {
                                        prod["qta"] = oldqta + qta
                                        db.collection("carts").document(user).collection("products").document(p.id.toString())
                                                .set(prod)
                                                .addOnSuccessListener {
                                                    Toast.makeText(this@PaginaProdotto,"Aggiunto al carrello",Toast.LENGTH_SHORT).show()
                                                    Log.d("qta", "Aggiunto prodotto $p con qta $qta totale ${prod["qta"]}")
                                                }
                                                .addOnFailureListener { e -> Log.w("---", "Errore aggiunta prodotto", e) }
                                  }
                                    }

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
                        val p= Prodotto(document.getLong("id")!!.toInt(), document.get("img")!!.toString(), document.get("titolo").toString(), document.get("desc").toString(), document.get("prezzo").toString(), document.getLong("qta")!!.toInt())
                        Log.d("pprod", "Prodotto vale $p")
                        myCallback.onCallback(p)
                    }

                }
            }
            .addOnFailureListener{ e -> Log.w("---", "Error getting document - GET PRODOTTO FROM DB", e)}
    }

}