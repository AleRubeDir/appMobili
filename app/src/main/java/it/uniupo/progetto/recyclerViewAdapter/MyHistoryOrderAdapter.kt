package it.uniupo.progetto.recyclerViewAdapter

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.R
import it.uniupo.progetto.fragments.OrderFragment.*

class MyHistoryOrderAdapter(private var ord: ArrayList<Order>, var tipo: String) : RecyclerView.Adapter<MyHistoryOrderAdapter.ViewHolder>() {
    lateinit var view : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.history_order, parent, false)
    return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(tipo=="Cliente"){
            val item = ord[position]
            checkRatings(item.id!!, holder.ratingQ, item.ratingQ, 1)
            checkRatings(item.id!!, holder.ratingV, item.ratingV, 2)
            checkRatings(item.id!!, holder.ratingC, item.ratingC, 3)
            if (item.tipo == "Carta") {
                holder.card.visibility = View.VISIBLE
            } else holder.cash.visibility = View.VISIBLE
            holder.date.text = item.date
            holder.cliente.text = item.cliente
            holder.rider.text = item.rider
        } else if(tipo=="Rider"){
            val item = ord[position]
            checkRatings(item.id!!, holder.ratingRC, item.ratingRC, 4)
            checkRatings(item.id!!, holder.ratingRP, item.ratingRP, 5)

            if (item.tipo == "Carta") {
                holder.card.visibility = View.VISIBLE
            } else holder.cash.visibility = View.VISIBLE
            holder.date.text = item.date
            holder.cliente.text = item.cliente
            holder.rider.text = item.rider
        }

    }

    private fun checkRatings(id : String, holder: RatingBar, rating: Int, type : Int) {
        if(rating==-1)
        {
            holder.setIsIndicator(false)
            holder.setOnRatingBarChangeListener{ ratingBar: RatingBar, fl: Float, b: Boolean ->

                AlertDialog.Builder(view.context)
                        .setPositiveButton("Conferma"){ dialog, which ->
                            var newrat = "ratingQ"
                            if(type==2) newrat = "ratingV"
                            else if(type==3) newrat = "ratingC"
                            else if(type==4) newrat = "ratingRC"
                            else if(type==5) newrat = "ratingRP"
                            val entry = hashMapOf<String, Any?>(
                                    newrat to ratingBar.progress,
                            )

                            val mail = FirebaseAuth.getInstance().currentUser!!.email.toString()
                            val db = FirebaseFirestore.getInstance()
                            db.collection("orders_history").document(id)
                                    .set(entry, SetOptions.merge())
                            holder.setIsIndicator(true)
                        }
                        .setTitle("Feedback")
                        .setMessage("Vuoi davvero assegnare questo voto?")
                        .setNeutralButton("Chiudi")
                        { _: DialogInterface, _: Int ->}
                        .show()

            }
        }
        else holder.progress = rating //va da 0 a 10. 1 = 1/2 stella
    }

    private fun showAlert() {
        AlertDialog.Builder(view.context)
                .setPositiveButton("Conferma"){ dialog, which -> }
                .setTitle("Vuoi assegnare questo voto?")
                .setMessage("Riceverai una notifica appena il rider partirà con il tuo ordine")
                .setNeutralButton("Chiudi")
                { _: DialogInterface, _: Int ->}
                .show()

    }
    override fun getItemCount():Int = ord.size

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var ratingQ: RatingBar = view.findViewById(R.id.rating_qualita)
        var ratingV: RatingBar = view.findViewById(R.id.rating_velocita)
        var ratingC: RatingBar = view.findViewById(R.id.rating_cortesia)
        var ratingRC: RatingBar = view.findViewById(R.id.rating_cortesia_cliente)
        var ratingRP: RatingBar = view.findViewById(R.id.rating_presenza)
        var cash: ImageView = view.findViewById(R.id.cash)
        var card: ImageView = view.findViewById(R.id.card)
        var date : TextView = view.findViewById(R.id.date)
        var cliente : TextView = view.findViewById(R.id.cliente)
        var rider : TextView = view.findViewById(R.id.rider)
    }

}
