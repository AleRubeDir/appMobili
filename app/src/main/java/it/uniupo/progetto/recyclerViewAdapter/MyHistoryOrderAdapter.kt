package it.uniupo.progetto.recyclerViewAdapter

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    val codRatingQ = 1
    val codRatingV = 2
    val codRatingC = 3
    val codRatingRC = 4
    val codRatingRP = 5
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.history_order, parent, false)
    return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ord[position]
        Log.d("riderHistory","item vale $item ")
        if(tipo=="Cliente") holder.rl_rider.visibility = View.INVISIBLE
        else if(tipo=="Rider") holder.rl_cliente.visibility = View.INVISIBLE
            checkRatings(item.id!!, holder.ratingQ, item.ratingQ, codRatingQ ,item.risultatoOrdine)
            checkRatings(item.id!!, holder.ratingV, item.ratingV, codRatingV,item.risultatoOrdine)
            checkRatings(item.id!!, holder.ratingC, item.ratingC, codRatingC,item.risultatoOrdine)
            checkRatings(item.id!!, holder.ratingRC, item.ratingRC, codRatingRC,item.risultatoOrdine)
            checkRatings(item.id!!, holder.ratingRP, item.ratingRP, codRatingRP,item.risultatoOrdine)
        Log.d("pagamento","item.tipo = ${item.tipo} risultato if = ${item.tipo=="Carta"}")
            if (item.tipo == "Carta") holder.card.setImageResource(R.drawable.ic_baseline_credit_card_24)
            holder.date.text = item.date
            holder.cliente.text = item.cliente
            holder.rider.text = item.rider
        holder.tv_orderId.text = item.id

        if(item.risultatoOrdine==0) holder.cv.setCardBackgroundColor(view.context.getColor(R.color.red))
        if(item.risultatoOrdine==-2) holder.cv.setCardBackgroundColor(view.context.getColor(R.color.gray))
            else  holder.cv.setCardBackgroundColor(view.context.getColor(R.color.green))
    }

    private fun checkRatings(id : String, holder: RatingBar, rating: Int?, type : Int, risultatoOrdine : Int?) {
            //voto ancora non assegnato, e utente non è gestore
        if(rating==-1 && tipo != "Gestore")
        {
            holder.setIsIndicator(false)
                         holder.setOnRatingBarChangeListener{ ratingBar: RatingBar, _, _ ->
                        if(risultatoOrdine==-2) { //ordine è rifiutato
                            Toast.makeText(view.context, "Non puoi valutare un ordine rifiutato", Toast.LENGTH_SHORT).show()
                            holder.progress = 0
                        }else{
                        AlertDialog.Builder(view.context)
                                .setPositiveButton("OKAY") { _, _ ->
                                    var newrat = "ratingQ"
                                    if (type == codRatingV) newrat = "ratingV"
                                    else if (type == codRatingC) newrat = "ratingC"
                                    else if (type == codRatingRC) newrat = "ratingRC"
                                    else if (type == codRatingRP) newrat = "ratingRP"
                                    val entry = hashMapOf<String, Any?>(
                                            newrat to ratingBar.progress,
                                    )

                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("orders_history").document(id)
                                            .set(entry, SetOptions.merge())
                                    holder.setIsIndicator(true)
                                }
                                .setTitle("Feedback")
                                .setMessage("Vuoi davvero assegnare questo voto?")
                                .setNegativeButton("Annulla"){ _, _ ->}
                                .show()
                    }
            }
            //rende selezionabili le stelle


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
