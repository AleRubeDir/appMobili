/*
package it.uniupo.progetto

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

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

}*/
