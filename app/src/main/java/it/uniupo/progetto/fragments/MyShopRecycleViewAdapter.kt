package it.uniupo.progetto.fragments

import android.content.Intent
import android.text.TextUtils.replace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.*


class MyShopRecycleViewAdapter(private val values: ArrayList<Prodotto>) : RecyclerView.Adapter<MyShopRecycleViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_shop, parent, false)
        val rel = view.findViewById<CardView>(R.id.rel)
        var id = view.findViewById<TextView>(R.id.id)
        rel.setOnClickListener{
            val intent = Intent(view.context, GestoreProdotto::class.java)
            intent.putExtra("id-prodotto", id.text )
            view.context.startActivity(intent)
        }
        return ViewHolder(view)
    }
    private fun makeCurrentFragment(fragment: Fragment) = HomeActivity().supportFragmentManager.beginTransaction().apply{
        replace(R.id.fl_wrapper,fragment)
        commit()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ItemFragment.stampaArray(values)
        val item = values[position]
        holder.titolo.text = item.titolo
        holder.prezzo.text = item.prezzo
        holder.img.setImageResource(item.img)
        holder.id.text= item.id.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titolo: TextView = view.findViewById(R.id.text)
        var img: ImageView = view.findViewById(R.id.img)
        var prezzo: TextView = view.findViewById(R.id.prezzo)
        var id : TextView = view.findViewById(R.id.id)

        override fun toString(): String {
            return super.toString() + " '" + titolo.text + "'" + prezzo.text
        }
    }

}
/*
class MyArrayAdapter(private val context: Activity, layout: Int, private val array: Array<Prodotto>): ArrayAdapter<Prodotto>(context,layout,array) {

    internal class ViewHolder{
        var text: TextView? = null
        var img: ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var myView = convertView
        if(myView==null) {
            //inserire elementi all'interno di ogni riga

            val vh = ViewHolder()
            myView = context.layoutInflater.inflate(R.layout.row, null)
            vh.text = myView!!.findViewById<TextView>(R.id.text)
            vh.img = myView.findViewById<ImageView>(R.id.img)

            vh.text!!.text = array[position].titolo
            Log.d("***","MAA -----> ${vh.text!!.text}")
            vh.img!!.setImageDrawable(context.getDrawable(array[position].img))

            myView.tag = vh
        }
        val holder = myView.tag as ViewHolder
        holder.text?.text = array[position].titolo
        holder.img?.setImageDrawable(context.getDrawable(array[position].img))
        return myView
    }

}
* */