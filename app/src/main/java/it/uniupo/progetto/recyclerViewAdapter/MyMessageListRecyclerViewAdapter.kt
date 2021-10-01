package it.uniupo.progetto.recyclerViewAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.Messaggio
import it.uniupo.progetto.R
import java.text.SimpleDateFormat
import java.util.*

class MyMessageListRecyclerViewAdapter(val values: ArrayList<Messaggio>, val rider : Int) : RecyclerView.Adapter<MyMessageListRecyclerViewAdapter.ViewHolder> () {
    lateinit var view : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMessageListRecyclerViewAdapter.ViewHolder {
        values.sort()
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.messages, parent, false)
        return ViewHolder(view)
    }
    override fun getItemViewType(position: Int): Int {
        return values[position].inviato
    }

    inner class ViewHolder(view: View) :  RecyclerView.ViewHolder(view){
        var testo : TextView = view.findViewById(R.id.messaggio)
        var ora : TextView = view.findViewById(R.id.ora)
        var testoS : TextView = view.findViewById(R.id.messaggioS)
        var oraS : TextView = view.findViewById(R.id.oraS)
        var rcvd : RelativeLayout = view.findViewById(R.id.rcvd)
        var send : RelativeLayout = view.findViewById(R.id.send)
        var date : RelativeLayout = view.findViewById(R.id.date)
        var date_container : TextView = view.findViewById(R.id.date_container)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var next = values[position]
        Log.d("mess"," ${values[position]}  pos = $position values.size = ${values.size} next = ${position+1}")
        if(position+1<values.size) {
            next = values[position + 1]
        }

        val item = values[position]
        val ora = item.ora.toDate().hours.toString()
        var minuti = item.ora.toDate().minutes.toString()
        Log.d("ora","${next.ora.seconds} != ${item.ora.seconds}??")
        if(next.ora.seconds != item.ora.seconds) {
            Log.d("ora","${next.ora.seconds} != ${item.ora.seconds}?? ${next.ora.seconds!=item.ora.seconds}")
            holder.date_container.text = convertLongToTime(item.ora.seconds)
        }

      //  holder.date_container.text = convertLongToTime(item.ora.seconds)
        if(minuti.length==1) minuti = "0"+ item.ora.toDate().minutes.toString()

        if(rider==1){
            if(item.inviato==0) { //inviato
                Log.d("Chats", "Messaggio ${item.testo} è stato inviato")
                holder.rcvd.visibility = View.INVISIBLE
                holder.testoS.text = item.testo
                holder.oraS.text = view.context.getString(R.string.orario,ora,minuti)
            }else if(item.inviato==1) { //ricevuto
                Log.d("Chats", "Messaggio ${item.testo} è stato ricevuto \n $holder")
                holder.send.visibility = View.INVISIBLE
                holder.testo.text = item.testo
                holder.ora.text = view.context.getString(R.string.orario,ora,minuti)
            }
        }else {
            if (item.inviato == 1) { //inviato
                Log.d("Chats", "Messaggio ${item.testo} è stato inviato")
                holder.rcvd.visibility = View.INVISIBLE
                holder.testoS.text = item.testo
                holder.oraS.text = view.context.getString(R.string.orario, ora, minuti)
            } else if (item.inviato == 0) { //ricevuto
                Log.d("Chats", "Messaggio ${item.testo} è stato ricevuto \n $holder")
                holder.send.visibility = View.INVISIBLE
                holder.testo.text = item.testo
                holder.ora.text = view.context.getString(R.string.orario, ora, minuti)
            }

        }
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time*1000)
        //  Log.d("mess","time vale $time date vale $date")
        val format = SimpleDateFormat("dd/MM/yyyy",Locale.ITALY)
        return format.format(date)
    }

    override fun getItemCount(): Int = values.size
}