package it.uniupo.progetto.recyclerViewAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import it.uniupo.progetto.HomeActivity
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R
import it.uniupo.progetto.StoricoOrdini.*

class MyHistoryOrderAdapter(private var ord: Order) : RecyclerView.Adapter<MyHistoryOrderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.history_order, parent, false)
    return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ord.arr[position]
        holder.rating.progress = ord.rating //va da 0 a 10. 1 = 1/2 stella
        Log.d("history","rating vale ${holder.rating.numStars}")
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

    override fun getItemCount():Int = 1

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var rating: RatingBar = view.findViewById(R.id.rating)
        var cash: ImageView = view.findViewById(R.id.cash)
        var card: ImageView = view.findViewById(R.id.card)
        var tot : TextView = view.findViewById(R.id.tot)
        var date : TextView = view.findViewById(R.id.date)
        var cliente : TextView = view.findViewById(R.id.cliente)
        var rider : TextView = view.findViewById(R.id.rider)
    }

}
