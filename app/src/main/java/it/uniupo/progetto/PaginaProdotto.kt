package it.uniupo.progetto

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.fragments.CartListFragment
import it.uniupo.progetto.fragments.ItemFragment
import it.uniupo.progetto.fragments.MyItemRecyclerViewAdapter
import java.util.HashMap

class PaginaProdotto  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagina_prodotto)
        val id = intent.getStringExtra("id-prodotto")!!
        var p: Prodotto

        getProdottoFromDB(id.toInt(), object: MyCallback {
            override fun onCallback(value: Prodotto) {
               // Log.d("prodotto", "value vale $value}")
                p = value
              //  Log.d("prodotto", "p vale $p}")
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

                img.setImageResource(p.img);
                val cart = findViewById<Button>(R.id.cart)
                cart.setOnClickListener{
                    Log.d("pprod","np value ${np.value}")
                    addToCart(p,np.value)
                    Toast.makeText(this@PaginaProdotto,"Aggiunto al carrello",Toast.LENGTH_SHORT).show()
                   /* supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_wrapper, CartListFragment())
                        commit()
                    }*/
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
       // var p = Prodotto(-1, -1, "ERR", "ERR", "2,99€", 2)
        val db = FirebaseFirestore.getInstance()
        db.collection("products")
                .get()
                .addOnSuccessListener { result->
                    for (document in result) {
                        Log.d("pprod", "id vale ${document.get("id").toString()} cerco $id")
                        if (document.get("id").toString() == id.toString()) {
                           var p= Prodotto(document.getLong("id")!!.toInt(), document.getLong("img")!!.toInt(), document.get("titolo").toString(), document.get("desc").toString(), document.get("prezzo").toString(), document.getLong("qta")!!.toInt())
                            Log.d("pprod", "Prodotto vale $p")
                            myCallback.onCallback(p)
                        }

                    }
                }
                .addOnFailureListener{ e -> Log.w("---", "Error getting document - GET PRODOTTO FROM DB", e)}
    }

}