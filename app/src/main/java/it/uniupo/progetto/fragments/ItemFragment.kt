package it.uniupo.progetto.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.HomeActivity
import it.uniupo.progetto.HomeActivity.Companion.array
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {
    private var columnCount = 1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                Log.d("***", "Values in item $array")
                getAllProducts((object: MyCallback {
                    override fun onCallback(value: List<Prodotto>) {
                        adapter = MyItemRecyclerViewAdapter(array)
                    }
                }))
            }
        }
        return view
    }

    companion object {
        interface MyCallback {
            fun onCallback(value: List<Prodotto>)
        }
        fun getAllProducts(myCallback: MyCallback) {
            val db = FirebaseFirestore.getInstance()
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
                                array.add(p)
                            }
                            myCallback.onCallback(array)

                        }
                    }

                    .addOnFailureListener { err->
                        Log.d("---", "Error getting document - ALL PRODUCTS() $err")
                    }
        }
    }
}