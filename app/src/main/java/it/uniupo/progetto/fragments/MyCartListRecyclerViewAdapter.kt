package it.uniupo.progetto.fragments


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R


class MyCartListRecyclerViewAdapter(
        private val values: ArrayList<Prodotto>
) : RecyclerView.Adapter<MyCartListRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_cart2, parent, false)

        val plus = view.findViewById<Button>(R.id.plus)
        val minus = view.findViewById<Button>(R.id.minus)
        val qta = view.findViewById<TextView>(R.id.qta)
        val idtv = view.findViewById<TextView>(R.id.id)
        plus.setOnClickListener {
            var id = idtv.text.toString().toInt()
            var n = qta.text.toString().toInt() + 1
            var p = Prodotto.getProdotto(id)
            Log.d("qta","prodotto $p vecchia qta ${qta.text} nuova qta $n")
            if (p != null) {
                if (p.qta>=n) {
                    qta.text = n.toString()
                    salvaQta(p,n)
                }else{
                    Toast.makeText(parent.context, "Qta max", Toast.LENGTH_SHORT).show()
                }
            }
        }
        plus.setOnLongClickListener {
            var id = idtv.text.toString().toInt()
            var n = qta.text.toString().toInt() + 10
            var p = Prodotto.getProdotto(id)
            if (p != null) {
                if (p.qta>n) {
                    qta.text = n.toString()
                    salvaQta(p,n)
                }else{
                    qta.text = p.qta.toString()
                    salvaQta(p,p.qta)
                    Toast.makeText(parent.context, "Qta max", Toast.LENGTH_SHORT).show()
                }
            }
            CartListFragment.cartTot()
            true
        }
        minus.setOnClickListener {
            var id = idtv.text.toString().toInt()
            var nqta = qta.text.toString().toInt() - 1
            var p = Prodotto.getProdotto(id)
            if (nqta <= 0) {
                rimuoviProdotto(id)
                notifyDataSetChanged()
                Toast.makeText(parent.context, "Prodotto rimosso ", Toast.LENGTH_SHORT).show()
            } else {
                diminuisciQta(id)
                salvaQta(p!!,nqta)
                qta.text = nqta.toString()
            }
            CartListFragment.cartTot()
        }
        //tieni premuto toglie 10 qta
        minus.setOnLongClickListener {
            var id = idtv.text.toString().toInt()
            var nqta = qta.text.toString().toInt() - 10
            var p = Prodotto.getProdotto(id)
            if (qta.text.toString().toInt() < 10) {
                rimuoviProdotto(id)
                notifyDataSetChanged()
                Toast.makeText(parent.context, "Prodotto rimosso ", Toast.LENGTH_SHORT).show()
            } else {
                diminuisciVeloceQta(id)
                salvaQta(p!!,nqta)
                qta.text = nqta.toString()
            }
            CartListFragment.cartTot()
            true
        }
        return ViewHolder(view)
    }
    private fun diminuisciQta(id: Int){
        for(p in values){
            if(p.id==id)
                p.qta--
        }
    }
    private fun diminuisciVeloceQta(id: Int){
        for(p in values){
            if(p.id==id)
                p.qta = p.qta-10
        }
    }
    private fun salvaQta(p : Prodotto, qta : Int){
        //val user = FirebaseAuth.getInstance().currentUser.toString() da rimettere quando faccio login
        val user = "fJB1nlkxu4GIPczWN6zH"
        val db = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String, Any?>(
                "qta" to qta,
        )
        db.collection("carts").document(user).collection("products").document(p.id.toString())
                .set(entry, SetOptions.merge())
                .addOnSuccessListener { documentReference ->
                    Log.d("qta", "Aggiornata qta carrello del prodotto ${p.id} : $qta ")
                }
                .addOnFailureListener { e -> Log.w("---", "Error adding document", e) }
    }
    private fun rimuoviProdotto(id: Int){
        var el : Prodotto = values[0]
        for(p in values){
            if(p.id==id)
                el = p
        }
        values.remove(el)
        //val user = FirebaseAuth.getInstance().currentUser.toString() da rimettere quando faccio login
        val user = "fJB1nlkxu4GIPczWN6zH"
        val db = FirebaseFirestore.getInstance()
        db.collection("carts").document(user).collection("products").document(id.toString())
                .delete()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
            holder.text.text = item.titolo
            holder.qta.text = item.qta.toString()
            holder.prezzo.text = item.prezzo
            holder.id.text = item.id.toString()

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text: TextView = view.findViewById(R.id.label)
        var qta: TextView = view.findViewById(R.id.qta)
        var prezzo: TextView = view.findViewById(R.id.prezzo)
        var id : TextView = view.findViewById(R.id.id)
        override fun toString(): String {
            return super.toString() + " text : ${text.text} ||| qta : ${qta .text}||| prezzo : ${prezzo.text} ||| id : ${id.text}"
        }
    }
}