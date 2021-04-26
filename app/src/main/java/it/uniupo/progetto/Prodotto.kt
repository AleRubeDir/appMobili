package it.uniupo.progetto

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import it.uniupo.progetto.fragments.ItemFragment
import java.io.Console
import java.io.Serializable
import java.util.*

class Prodotto(val id: Int, val img: Int, val titolo: String, val desc: String, val prezzo: String, var qta: Int) : Serializable {


    override fun toString(): String {
        return "$id - $titolo - $desc - $prezzo - $qta - $img"
    }
    companion object {

        fun getProdotto(id: Int): Prodotto? {
            for (p in HomeActivity.array) {
                if (p.id == id)
                    return p
            }
            return null
        }
        fun caricaProdotto(img: Int, titolo: String, desc: String, prezzo: String, qta: Int) : Prodotto {
            val db = FirebaseFirestore.getInstance()
            val id = getLastID() + 1
           // Log.d("***","GETLASTID MI HA DATO $id")
            val prod: MutableMap<String, Any> = HashMap()
            prod["id"] = id
            prod["titolo"] = titolo
            prod["desc"] = desc
            prod["img"] = img
            prod["prezzo"] = prezzo
            prod["qta"] = qta
            val p = Prodotto(id,img,titolo,desc,prezzo,qta)
            db.collection("products").document(prod["id"].toString())
                    .set(prod)
                    .addOnSuccessListener {
                        Log.d("***","Aggiunto prodotto $prod")
                    }
                    .addOnFailureListener{ e -> Log.w("---","Errore aggiunta prodotto",e)}
            return p
        }

        fun getLastID() : Int{
            var max = 5
        /*    val values = getAllProducts()

            for(p in values){
                if(p.id>max){
                    max = p.id
                }
            }*/
            return max+1
        }

    }
}