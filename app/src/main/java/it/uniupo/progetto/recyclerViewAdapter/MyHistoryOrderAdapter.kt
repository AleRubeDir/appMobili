package it.uniupo.progetto.recyclerViewAdapter

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.R
import it.uniupo.progetto.fragments.OrderFragment.*
import kotlin.coroutines.coroutineContext

class MyHistoryOrderAdapter(private var ord: ArrayList<Order>, var tipo: String) : RecyclerView.Adapter<MyHistoryOrderAdapter.ViewHolder>() {
    lateinit var view : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.history_order, parent, false)
    return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = ord[position]
        if(tipo=="Cliente"){
 /*           checkRatings(item.id!!, holder.ratingQ, item.ratingQ, 1)
            checkRatings(item.id!!, holder.ratingV, item.ratingV, 2)
            checkRatings(item.id!!, holder.ratingC, item.ratingC, 3)*/
            holder.rl_rider.visibility = View.INVISIBLE
        }
        else if(tipo=="Rider"){
      /*      checkRatings(item.id!!, holder.ratingRC, item.ratingRC, 4)
            checkRatings(item.id!!, holder.ratingRP, item.ratingRP, 5)
            */
            holder.rl_cliente.visibility = View.INVISIBLE
        }
            checkRatings(item.id!!, holder.ratingQ, item.ratingQ, 1)
            checkRatings(item.id!!, holder.ratingV, item.ratingV, 2)
            checkRatings(item.id!!, holder.ratingC, item.ratingC, 3)
            checkRatings(item.id!!, holder.ratingRC, item.ratingRC, 4)
            checkRatings(item.id!!, holder.ratingRP, item.ratingRP, 5)
        Log.d("pagamento","item.tipo = ${item.tipo} risultato if = ${item.tipo=="Carta"}")
            if (item.tipo == "Carta") holder.card.setImageResource(R.drawable.ic_baseline_credit_card_24)
            holder.date.text = item.date
            holder.cliente.text = item.cliente
            holder.rider.text = item.rider
        holder.tv_orderId.text = item.id

        if(item.risultatoOrdine==0) holder.cv.setCardBackgroundColor(view.context.getColor(R.color.red))
            else  holder.cv.setCardBackgroundColor(view.context.getColor(R.color.green))
    }

    private fun checkRatings(id : String, holder: RatingBar, rating: Int?, type : Int) {
        if(rating==-1 && tipo != "Gestore")
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
        else if(rating!=null) holder.progress = rating //va da 0 a 10. 1 = 1/2 stella
    }

    override fun getItemCount():Int = ord.size

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var ratingQ: RatingBar = view.findViewById(R.id.rating_qualita)
        var ratingV: RatingBar = view.findViewById(R.id.rating_velocita)
        var ratingC: RatingBar = view.findViewById(R.id.rating_cortesia)
        var ratingRC: RatingBar = view.findViewById(R.id.rating_cortesia_cliente)
        var ratingRP: RatingBar = view.findViewById(R.id.rating_presenza)

        var rl_cliente : RelativeLayout = view.findViewById(R.id.val_cliente)
        var rl_rider : RelativeLayout = view.findViewById(R.id.val_rider)

        var card: ImageView = view.findViewById(R.id.card)
        var date : TextView = view.findViewById(R.id.date)
        var tv_orderId : TextView = view.findViewById(R.id.orderId)
        var cliente : TextView = view.findViewById(R.id.cliente)
        var rider : TextView = view.findViewById(R.id.rider)

        var cv : CardView = view.findViewById(R.id.order)
    }

}
