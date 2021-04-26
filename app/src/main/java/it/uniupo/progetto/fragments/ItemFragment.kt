package it.uniupo.progetto.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
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


        Log.d("***", "Values in item $array")
        // Set the adapter
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
    private fun makeCurrentFragment(fragment: Fragment) = HomeActivity().supportFragmentManager.beginTransaction().apply{
        replace(R.id.fl_wrapper, fragment)
        commit()
    }
private fun Fragment.addChildFragment(fragment: Fragment, frameId: Int) {
    val transaction = childFragmentManager.beginTransaction()
    transaction.replace(frameId, fragment).commit()
}

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
                            val img = document.getLong("img")!!.toInt()
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

                .addOnFailureListener {
                    //  arr.add(Prodotto(2,-1,"q","q","2,00€",2))
                    Log.d("---", "Error getting document - ALL PRODUCTS()")
                }
    }


    companion object {
        fun stampaArray(array: ArrayList<Prodotto>) {
            Log.d("***", "$array")
        }
    }
}