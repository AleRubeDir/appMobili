package it.uniupo.progetto.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.HomeActivity
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R
import java.util.HashMap
import kotlin.concurrent.thread

/**
 * A fragment representing a list of Items.
 */
class CartListFragment : Fragment() {

    private var columnCount = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart_list2, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                getUserCart((object: ItemFragment.MyCallback {
                    override fun onCallback(value: List<Prodotto>) {
                        cartTot()
                        //Log.d("totale","Totale in callback vale ${HomeActivity.tot}")
                        adapter = MyCartListRecyclerViewAdapter(HomeActivity.carrello)
                    }
                }))
            }
        }
        return view
    }

/*    fun cartTot(arr : ArrayList<Prodotto>) {
        if(arr.isNotEmpty()) {
            var temp = 0.0
            for (p in arr) {
                Log.d("totale", "p vale $p")
                Log.d("totale", "valore : ${p.prezzo.toDouble() * p.qta}")
                HomeActivity.tot += (p.prezzo.toDouble() * p.qta)
                Log.d("totale", "prezzo ${p.prezzo.toDouble() * p.qta} qta ${p.qta}Temp dentro vale $temp Totale dentro vale ${HomeActivity.tot}")
            }
            //HomeActivity.tot = temp
            Log.d("totale", "Temp vale $temp Totale in cartTot vale ${HomeActivity.tot}")
        }
    }*/
    private fun getUserCart(myCallback: ItemFragment.MyCallback) {
        val user = FirebaseAuth.getInstance().currentUser!!.email
        HomeActivity.carrello.clear()
        val db = FirebaseFirestore.getInstance()
        db.collection("carts").document(user!!).collection("products")
            .get()
            .addOnSuccessListener { result ->
                        for (document in result) {
                            val id = document.getLong("id")!!.toInt()
                            val titolo = document.get("titolo").toString()
                            val prezzo = document.get("prezzo").toString()
                            val qta = document.getLong("qta")!!.toInt()
                            val p = Prodotto(id, -1, titolo, "NULL", prezzo, qta)
                            HomeActivity.carrello.add(p)
                        }
                stampaArray(HomeActivity.carrello)
                    myCallback.onCallback(HomeActivity.carrello)
            }
            .addOnFailureListener {
                Log.d("totale", "Error getting document - get user cart()")
            }
    }
    fun stampaArray(array : ArrayList<Prodotto>){
        Log.d("totale","clf --- $array")
    }

    companion object{
        fun cartTot() {
            if(HomeActivity.carrello.isNotEmpty()) {
                HomeActivity.tot = 0.0
                for (p in HomeActivity.carrello) {
                    HomeActivity.tot += (p.prezzo.toDouble() * p.qta)
                   // HomeActivity().updateTot()
                    //HomeActivity.x++
                }
            }
        }
    }
}