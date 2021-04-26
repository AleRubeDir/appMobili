package it.uniupo.progetto.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.HomeActivity
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R


class MyItemRecyclerViewAdapter(private val values: ArrayList<Prodotto>) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Log.d("***", "Values___ in mirva $values")
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)

        /*val rel = view.findViewById<RelativeLayout>(R.id.rel)
        var id = view.findViewById<TextView>(R.id.id)
        var fp = FragmentProdotto()
        rel.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("id", id.text.toString()) // Put anything what you want
            fp.arguments = bundle
            makeCurrentFragment(fp)
        }*/
        return ViewHolder(view)
    }
//    private fun makeCurrentFragment(fragment: Fragment) = HomeActivity().supportFragmentManager.beginTransaction().apply{
//        replace(R.id.fl_wrapper, fragment)
//        commit()
//    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ItemFragment.stampaArray(values)

        val item = values[position]
/*        Log.d("bar","${R.drawable.barilla_spaghetti} --S-- ${item.img}")
        Log.d("bar","${R.drawable.barilla_tortiglioni} --T-- ${item.img}")*/
        holder.titolo.text = item.titolo
        holder.prezzo.text = item.prezzo
        holder.img.setImageResource(item.img)
        holder.id.text= item.id.toString()
        //holder.img!!.setImageDrawable(ContextCompat.getDrawable(HomeActivity(),values[position].img))
       // Log.d("***","Ciao ${MainActivity().getDrawable(values[position].img)}")
       // holder.img.setImageDrawable(MainActivity().getDrawable(values[position].img))

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