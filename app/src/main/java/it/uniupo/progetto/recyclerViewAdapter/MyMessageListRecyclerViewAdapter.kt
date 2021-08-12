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
import java.lang.Long.parseLong
import java.text.SimpleDateFormat
import java.util.*

class MyMessageListRecyclerViewAdapter(val values: ArrayList<Messaggio>) : RecyclerView.Adapter<MyMessageListRecyclerViewAdapter.ViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMessageListRecyclerViewAdapter.ViewHolder {
        values.sort()
        val view = LayoutInflater.from(parent.context)
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
        var ora = item.ora.toDate().hours.toString()
        var minuti = item.ora.toDate().minutes.toString()
        var mexDate = item.ora.toDate()
        var mexDate2 = item.ora

      //  Log.d("mess","----- ${next.ora.toDate()} !=  $mexDate =  ${next.ora.toDate()!=mexDate}")
        Log.d("mess","----- ${convertLongToTime(next.ora.seconds)} !=  ${convertLongToTime(mexDate2.seconds)} =  ${convertLongToTime(next.ora.seconds) != convertLongToTime(mexDate2.seconds)}")
        val output: String = convertLongToTime(item.ora.seconds)
        holder.date.visibility = View.VISIBLE
        holder.date_container.text = output
        if(convertLongToTime(next.ora.seconds) != convertLongToTime(mexDate2.seconds)) {
         //   if (Date() != mexDate) {
                holder.date.visibility = View.INVISIBLE
             //   if (holder.date_container.text != output)
              //      holder.date_container.text = output
          //  }
        }
        if(minuti.length==1) minuti = "0"+ item.ora.toDate().minutes.toString()

        if(item.inviato==1) { //inviato
            Log.d("Chats", "Messaggio ${item.testo} è stato inviato")
            holder.rcvd.visibility = View.INVISIBLE
            holder.testoS.text = item.testo
            holder.oraS.text = "$ora:$minuti"
        }else if(item.inviato==0) { //ricevuto
            Log.d("Chats", "Messaggio ${item.testo} è stato ricevuto \n $holder")
            holder.send.visibility = View.INVISIBLE
            holder.testo.text = item.testo
            holder.ora.text = "$ora:$minuti"
        }

    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time*1000)
      //  Log.d("mess","time vale $time date vale $date")
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }

    override fun getItemCount(): Int = values.size
}

