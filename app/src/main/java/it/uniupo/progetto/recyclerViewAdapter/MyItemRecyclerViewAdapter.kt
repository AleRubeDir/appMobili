package it.uniupo.progetto.recyclerViewAdapter
import android.content.Intent
import android.text.TextUtils.replace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import it.uniupo.progetto.HomeActivity
import it.uniupo.progetto.PaginaProdotto
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R


class MyItemRecyclerViewAdapter(private val values: ArrayList<Prodotto>) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)

        val rel = view.findViewById<CardView>(R.id.rel)
        var id = view.findViewById<TextView>(R.id.id)
        rel.setOnClickListener{
            val intent = Intent(view.context, PaginaProdotto::class.java)
            intent.putExtra("id-prodotto", id.text )
            view.context.startActivity(intent)
        }
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         val item = values[position]
        if(item.qta != 0) {
            holder.titolo.text = item.titolo
            holder.prezzo.text = holder.itemView.context.getString(R.string.cash, item.prezzo)
            Picasso.get().load(item.img).into(holder.img)
            holder.id.text = item.id.toString()
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titolo: TextView = view.findViewById(R.id.text)
        var img: ImageView = view.findViewById(R.id.img)
        var prezzo: TextView = view.findViewById(R.id.prezzo)
        var id : TextView = view.findViewById(R.id.id)
        var rel : CardView = view.findViewById(R.id.rel)
        override fun toString(): String {
            return super.toString() + " '" + titolo.text + "'" + prezzo.text
        }
    }
}