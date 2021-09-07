package it.uniupo.progetto.recyclerViewAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import it.uniupo.progetto.GestoreProdotto
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R


class MyShopRecycleViewAdapter(private val values: ArrayList<Prodotto>) : RecyclerView.Adapter<MyShopRecycleViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_shop, parent, false)
        val rel = view.findViewById<CardView>(R.id.rel)
        var id = view.findViewById<TextView>(R.id.id)
        rel.setOnClickListener{
            val intent = Intent(view.context, GestoreProdotto::class.java)
            intent.putExtra("id-prodotto", id.text)
            view.context.startActivity(intent)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        if(item.qta==0) holder.color.setBackgroundResource(R.drawable.gradient_red)

        holder.titolo.text = item.titolo
        holder.prezzo.text = holder.itemView.context.getString(R.string.cash,item.prezzo)
        Picasso.get().load(item.img).into(holder.img)
        holder.id.text= item.id.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titolo: TextView = view.findViewById(R.id.text)
        var img: ImageView = view.findViewById(R.id.img)
        var prezzo: TextView = view.findViewById(R.id.prezzo)
        var id : TextView = view.findViewById(R.id.id)
        var color : RelativeLayout = view.findViewById(R.id.color)
    }

}
