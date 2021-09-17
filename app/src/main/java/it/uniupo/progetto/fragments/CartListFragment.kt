package it.uniupo.progetto.fragments


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.*
import it.uniupo.progetto.recyclerViewAdapter.*
/**
 * A fragment representing a list of Items.
 */
class CartListFragment : Fragment()  {

    private var columnCount = 1
    lateinit var tot : TextView
    lateinit var intent : Intent
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart_list2, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        tot = view.findViewById(R.id.tot)

        recyclerView.layoutManager =  LinearLayoutManager(view.context)
        getUserCart((object : ItemFragment.Companion.MyCallback {
            override fun onCallback(value: List<Prodotto>) {
                cartTot()
                var totdoub = "%.2f".format(ClienteActivity.tot)
                tot.text = activity?.getString(R.string.cash, totdoub)
                recyclerView.adapter = MyCartListRecyclerViewAdapter(ClienteActivity.carrello)
                val db = FirebaseFirestore.getInstance()
                val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
                db.collection("carts").document(email).collection("products")
                        .addSnapshotListener { snap, e ->
                            if (e != null) Log.d("mysnap", "Errore connessione $e")
                            if (snap != null)
                                cartTot()
                            totdoub = "%.2f".format(ClienteActivity.tot)
                            tot.text = activity?.getString(R.string.cash, totdoub)
                        }
            }
        }))
        val compra = view.findViewById<Button>(R.id.compra)
        compra.setOnClickListener{
            hasOrderPending(object : MyCallback2 {
                override fun onCallback(ris: Boolean) {
                    Log.d("secondo","ris vale $ris")
                    if(!ris){
                        impostaOrdine(ClienteActivity.carrello, object : MyCallback{
                            override fun onCallback(ordId: String) {
                                intent = Intent(view.context, PagamentoActivity::class.java)
                                intent.putExtra("ord_id",ordId)
                                startActivity(intent)
                            }
                        })
                    }else{
                        AlertDialog.Builder(view.context)
                                .setTitle("Ordine attivo")
                                .setMessage("Non puoi effettuare un altro ordine, aspetta di completare quello attivo")
                                .setPositiveButton("Accetta")
                                { _: DialogInterface, _: Int ->
                                }
                                .show()
                    }
                }

            })


        }

        val swipegesture = object: SwipeGesture(view.context){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                MyCartListRecyclerViewAdapter(ClienteActivity.carrello).rimuoviProdottoSwipe(viewHolder.bindingAdapterPosition)
                recyclerView.adapter = MyCartListRecyclerViewAdapter(ClienteActivity.carrello)
            }
        }
        val touchelper = ItemTouchHelper(swipegesture)
        touchelper.attachToRecyclerView(recyclerView)
        return view
    }
    interface MyCallback2{
        fun onCallback(ris : Boolean)
    }

    fun hasOrderPending(myCallback: MyCallback2) {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
        var ris = false
        db.collection("orders").document(email).collection("order").get()
                .addOnSuccessListener {
                    for (d in it) {
                        ris = true
                    }
                        myCallback.onCallback(ris)
                }
                .addOnFailureListener { e ->
                    Log.d("secondo","failure listener")
                    e.printStackTrace()
                }
    }

    fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..8)
                .map { allowedChars.random() }
                .joinToString("")
    }
    private fun impostaOrdine(carrello: ArrayList<Prodotto>, mycallback : MyCallback) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser?.email.toString()
        val ordId = getRandomString()
        var dummy = hashMapOf<String, Any?>(
                " " to " "
        )
                    for (p in carrello) {
                        var entry = hashMapOf<String, Any?>(
                                "id" to p.id,
                                "titolo" to p.titolo,
                                "qta" to p.qta,
                                "prezzo" to p.prezzo,
                        )
                        db.collection("orders").document(user).set(dummy) //se no errore scritte in corsivo in firebase
                        db.collection("orders").document(user).collection("order").document(ordId).set(dummy) //se no errore scritte in corsivo in firebase
                        db.collection("orders").document(user).collection("order").document(ordId).collection("products").document(p.id.toString())
                                .set(entry)
                                .addOnSuccessListener {
                                    Log.d("carrello", "Ordine piazzato con successo")
                                    mycallback.onCallback(ordId)
                                }
                                .addOnFailureListener {
                                    Log.d("carrello", "Errore ordine $it")
                                }
                    }

    }
    interface MyCallback{
        fun onCallback(ordId : String)
    }

    fun cartTot() {
      ClienteActivity.tot = 0.0;
            for (p in ClienteActivity.carrello) {
                ClienteActivity.tot += (p.prezzo.toDouble() * p.qta)
            }
        }

    private fun getUserCart(myCallback: ItemFragment.Companion.MyCallback) {
        val user = FirebaseAuth.getInstance().currentUser!!.email
        ClienteActivity.carrello.clear()
        val db = FirebaseFirestore.getInstance()
        db.collection("carts").document(user!!).collection("products")
            .get()
            .addOnSuccessListener { result ->
                        for (document in result) {
                            val id = document.getLong("id")!!.toInt()
                            val titolo = document.get("titolo").toString()
                            val prezzo = document.get("prezzo").toString()
                            val qta = document.getLong("qta")!!.toInt()
                            val p = Prodotto(id, "-1", titolo, "NULL", prezzo, qta)
                            ClienteActivity.carrello.add(p)
                        }
                stampaArray(ClienteActivity.carrello)
                    myCallback.onCallback(ClienteActivity.carrello)
            }
            .addOnFailureListener {
                Log.d("totale", "Error getting document - get user cart()")
            }
    }
    fun stampaArray(array: ArrayList<Prodotto>){
        Log.d("totale", "clf --- $array")
    }

}