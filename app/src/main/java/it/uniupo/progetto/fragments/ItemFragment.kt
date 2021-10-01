package it.uniupo.progetto.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.ClienteActivity.Companion.array
import it.uniupo.progetto.LoginActivity
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R
import it.uniupo.progetto.recyclerViewAdapter.*
/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {
    private var columnCount = 1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.myList)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        getAllProducts((object: MyCallback {
            override fun onCallback(value: List<Prodotto>) {
                recyclerView.adapter = MyItemRecyclerViewAdapter(array)
            }
        }))

        return view
    }

    companion object {
        interface MyCallback {
            fun onCallback(value: List<Prodotto>)
        }

        private fun getUserType(user: String?, fc: LoginActivity.FirestoreCallback){
            val db = FirebaseFirestore.getInstance()
            var t = "null"
            db.collection("users").document(user!!)
                    .get()
                    .addOnSuccessListener { result ->
                        if(!result.getString("type").isNullOrBlank())
                            t = result.getString("type")!!
                        fc.onCallback(t)
                    }
        }

        fun getAllProducts(myCallback: MyCallback) {
            val db = FirebaseFirestore.getInstance()
            val fb = FirebaseAuth.getInstance()
            val email = fb.currentUser?.email
            getUserType(email, object : LoginActivity.FirestoreCallback {
                override fun onCallback(type: String) {
            array.clear()
            db.collection("products")
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            var p: Prodotto
                            for (document in it.result!!) {
                                val id = document.getLong("id")!!.toInt()
                                val img = document.get("img")!!.toString()
                                val titolo = document.get("titolo").toString()
                                val desc = document.get("desc").toString()
                                val prezzo = document.get("prezzo").toString()
                                val qta = document.getLong("qta")!!.toInt()
                                p = Prodotto(id, img, titolo, desc, prezzo, qta)
                                Log.d("***", "in getAllProducts trovo : $p")
                                if(p.qta>0 && type=="Cliente" || type=="Gestore")
                                    array.add(p)
                            }
                            myCallback.onCallback(array)

                        }
                    }
                    .addOnFailureListener { err->
                        Log.d("---", "Error getting document - ALL PRODUCTS() $err")
                    }
                }
            })
        }

    }
}