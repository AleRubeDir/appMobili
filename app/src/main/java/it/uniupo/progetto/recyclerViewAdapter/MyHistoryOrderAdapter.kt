package it.uniupo.progetto.recyclerViewAdapter

import android.content.DialogInterface
import android.util.Log
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
import it.uniupo.progetto.StoricoOrdini.*

class MyHistoryOrderAdapter(private var ord: Order) : RecyclerView.Adapter<MyHistoryOrderAdapter.ViewHolder>() {
    lateinit var view : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.history_order, parent, false)
    return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        checkRatings(holder.ratingQ,ord.ratingQ,1)
        checkRatings(holder.ratingV,ord.ratingV,2)
        checkRatings(holder.ratingC,ord.ratingC,3)

        if(ord.tipo=="Carta"){
            holder.card.visibility= View.VISIBLE
            holder.cash.visibility= View.INVISIBLE
        }
        Log.d("history","tot vale ${holder.tot.text.toString().toDouble()}")
       // holder.tot.text = (holder.tot.text.toString().toDouble() + item.prezzo.toDouble()*item.qta).toString()
        holder.tot.text = ord.tot
        holder.date.text = ord.date
        holder.cliente.text = ord.cliente
        holder.rider.text = ord.rider


    }

    private fun checkRatings(holder: RatingBar, rating: Int, type : Int) {
        if(rating==-1)
        {
            holder.setIsIndicator(false)
            holder.setOnRatingBarChangeListener{ ratingBar: RatingBar, fl: Float, b: Boolean ->

                AlertDialog.Builder(view.context)
                        .setPositiveButton("Conferma"){ dialog, which ->
                            var newrat = "ratingQ"
                            if(type==2) newrat = "ratingV"
                            else if(type==3) newrat = "ratingC"
                            val entry = hashMapOf<String, Any?>(
                                    newrat to ratingBar.progress,
                            )
                            Log.d("rating","id vale ${ord.id} \n entry vale $entry")
                            val mail = FirebaseAuth.getInstance().currentUser!!.email.toString()
                            val db = FirebaseFirestore.getInstance()
                            db.collection("orders_history").document(mail).collection("orders").document(ord.id!!).collection("others").document("info")
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
    override fun getItemCount():Int = 1

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var ratingQ: RatingBar = view.findViewById(R.id.rating_qualita)
        var ratingV: RatingBar = view.findViewById(R.id.rating_velocita)
        var ratingC: RatingBar = view.findViewById(R.id.rating_cortesia)
        var cash: ImageView = view.findViewById(R.id.cash)
        var card: ImageView = view.findViewById(R.id.card)
        var tot : TextView = view.findViewById(R.id.tot)
        var date : TextView = view.findViewById(R.id.date)
        var cliente : TextView = view.findViewById(R.id.cliente)
        var rider : TextView = view.findViewById(R.id.rider)
    }

}
