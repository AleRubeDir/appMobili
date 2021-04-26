/*
package it.uniupo.progetto

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

class CartAdapter(private val context: Activity, array: MutableList<Prodotto>):
    ArrayAdapter<Prodotto>(context,R.layout.cart_row, array) {
    private val array: MutableList<Prodotto> = array

    //per riutilizzare basta cambiare i campi di ViewHolder come quelli messi nell'activity_main
    internal class ViewHolder{
        var text: TextView? = null
        var qta: TextView? = null
        var prezzo: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var myView : View?
        if(convertView==null) {
            //inserire elementi all'interno di ogni riga

            val vh = ViewHolder()
            myView = context.layoutInflater.inflate(R.layout.cart_row, null)

            vh.text = myView!!.findViewById<TextView>(R.id.label) as TextView
            vh.text!!.setTextColor(Color.BLACK)
            vh.qta = myView!!.findViewById<TextView>(R.id.qta) as TextView
            vh.qta!!.setTextColor(Color.BLACK)
            vh.prezzo = myView!!.findViewById<TextView>(R.id.prezzo) as TextView
            vh.prezzo!!.setTextColor(Color.BLACK)


            myView.tag = vh
        }else{
            myView = convertView
        }
        val holder = myView!!.tag as ViewHolder
        holder.text?.text = array[position].titolo
        holder.qta?.text  = array[position].qta.toString()
        holder.prezzo?.text =array[position].prezzo

        return myView
    }

}
*/
