package it.uniupo.progetto.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.HomeActivity
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R
import java.util.*


class FragmentProdotto : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val bundle = this.arguments
        if (bundle != null) {
            var p: Prodotto
            getProdottoFromDB(bundle.getString("id")!!.toInt(), object : MyCallback {
                override fun onCallback(value: Prodotto) {
                    //Log.d("prodotto", "value vale $value}")
                    p = value
                   // Log.d("prodotto", "p vale $p}")
                    val tvTitolo = view?.findViewById<TextView>(R.id.titolo)
                    tvTitolo?.text = p.titolo
                    val desc = view?.findViewById<TextView>(R.id.desc)
                    desc?.text = p.desc
                    val prezzo = view?.findViewById<TextView>(R.id.prezzo)
                    prezzo?.text = p.prezzo
                    val np = view?.findViewById<NumberPicker>(R.id.qta)
                    np?.minValue = 1
                    np?.maxValue = p.qta
                    val img = view?.findViewById<ImageView>(R.id.img)
                   // Log.d("prodotto", "imageview vale $img e deve contenere ${p.img} ")
                    //img.setImageResource(p.img)
                    val cart = view?.findViewById<ImageButton>(R.id.cart)
                    cart?.setOnClickListener {
                        addToCart(p, np!!.value)
                        Toast.makeText(HomeActivity(), "Aggiunto al carrello", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        return inflater.inflate(R.layout.fragment_prodotto, container, false)
    }



    fun addToCart(p: Prodotto, qta: Int) {
        val db = FirebaseFirestore.getInstance()
        //var auth : FirebaseAuth
        //auth = FirebaseAuth.getInstance();
        //val user = auth.currentUser
        val prod: MutableMap<String, Any> = HashMap()
        prod["id"] = p.id
        prod["titolo"] = p.titolo
        prod["prezzo"] = p.prezzo
        prod["qta"] = qta
        db.collection("carts").document("fJB1nlkxu4GIPczWN6zH").collection("products").document(p.id.toString())
                .get()
                .addOnSuccessListener { result->
                    prod["qta"] = result.get("qta").toString().toInt() + qta
                    db.collection("carts").document("fJB1nlkxu4GIPczWN6zH").collection("products").document(p.id.toString())
                            .set(prod)
                            .addOnSuccessListener {
                                Log.d("qta", "Aggiunto prodotto $p con qta $qta")
                            }
                            .addOnFailureListener { e -> Log.w("---", "Errore aggiunta prodotto", e) }
                }
    }

    interface MyCallback{
        fun onCallback(value: Prodotto)
    }

    fun getProdottoFromDB(id: Int, myCallback: MyCallback){
        var p = Prodotto(-1, "-1", "ERR", "ERR", "2,99€", 2)
        val db = FirebaseFirestore.getInstance()
        db.collection("products")
                .get()
                .addOnSuccessListener { result->
                    for (document in result) {
                        Log.d("prodotto", "${document.get("id")} = $id")
                        if (document.get("id") == id) {
                            p= Prodotto(document.getLong("id")!!.toInt(), document.get("img")!!.toString(), document.get("titolo").toString(), document.get("desc").toString(), document.get("prezzo").toString(), document.getLong("qta")!!.toInt())
                            Log.d("prodotto", "in getProdottoFromDB p vale $p")
                        }

                        myCallback.onCallback(p)
                    }
                }
                .addOnFailureListener{ e -> Log.w("---", "Error getting document - GET PRODOTTO FROM DB", e)}
    }


}


