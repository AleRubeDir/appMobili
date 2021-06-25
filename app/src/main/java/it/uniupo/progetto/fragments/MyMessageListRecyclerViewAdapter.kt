package it.uniupo.progetto.fragments

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.Messaggio
import it.uniupo.progetto.R
import kotlin.collections.ArrayList

class MyMessageListRecyclerViewAdapter (val values: ArrayList<Messaggio>) : RecyclerView.Adapter<MyMessageListRecyclerViewAdapter.ViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMessageListRecyclerViewAdapter.ViewHolder {
        values.sort()
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.messages, parent, false)
        return ViewHolder(view)
    }
    override fun getItemViewType(position: Int): Int {
        return values[position].inviato
    }

    inner class ViewHolder (view : View) :  RecyclerView.ViewHolder(view){
        var testo : TextView = view.findViewById(R.id.messaggio)
        var ora : TextView = view.findViewById(R.id.ora)
        var testoS : TextView = view.findViewById(R.id.messaggioS)
        var oraS : TextView = view.findViewById(R.id.oraS)
        var rcvd : RelativeLayout = view.findViewById(R.id.rcvd)
        var send : RelativeLayout = view.findViewById(R.id.send)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("Chats","vvvvvvvvvvvOBVH")
        val item = values[position]
        var ora = item.ora.toDate().hours.toString()
        var minuti = item.ora.toDate().minutes.toString()
        if(minuti.length==1) minuti = "0"+ item.ora.toDate().minutes.toString()

        if(item.inviato==1) { //inviato
            Log.d("Chats","Messaggio ${item.testo} è stato inviato")
            holder.rcvd.visibility = View.INVISIBLE
            holder.testoS.text = item.testo
            holder.oraS.text = "$ora:$minuti"
        }else if(item.inviato==0) { //ricevuto
            Log.d("Chats","Messaggio ${item.testo} è stato ricevuto \n $holder")
            holder.send.visibility = View.INVISIBLE
            holder.testo.text = item.testo
            holder.ora.text = "$ora:$minuti"
        }


        Log.d("Chats","^^^^^^^^^OBVH")


    }
    override fun getItemCount(): Int = values.size
}


/*
*
* package it.uniupo.progetto.fragments

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.Messaggio
import it.uniupo.progetto.R
import kotlin.collections.ArrayList

class MyMessageListRecyclerViewAdapter (val values: ArrayList<Messaggio>) : RecyclerView.Adapter<RecyclerView.ViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        values.sort()
        val viewRcv = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_received, parent, false)

        val viewSend = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_send, parent, false)
        if(viewType==0) return ViewHolderReceive(viewRcv)
        else return ViewHolderSend(viewSend)


    }
    override fun getItemViewType(position: Int): Int {
         return values[position].inviato
    }

    inner class ViewHolderSend (view: View) : RecyclerView.ViewHolder(view) {
        var testo : TextView = view.findViewById(R.id.messaggio)
        var ora : TextView = view.findViewById(R.id.ora)
    }
    inner class ViewHolderReceive (view: View) : RecyclerView.ViewHolder(view) {
        var testoR : TextView = view.findViewById(R.id.messaggio)
        var oraR : TextView = view.findViewById(R.id.ora)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("Chats","vvvvvvvvvvvOBVH")
        val item = values[position]
        if(item.inviato==1) { //inviato
            Log.d("Chats","Messaggio ${item.testo} è stato inviato")
            holder as ViewHolderSend
            holder.testo.text = item.testo
            holder.ora.text = item.ora
        }else if(item.inviato==0) { //ricevuto
            Log.d("Chats","Messaggio ${item.testo} è stato ricevuto \n $holder")
            holder as ViewHolderReceive
            holder.testoR.text = item.testo
            holder.oraR.text = item.ora
        }


        Log.d("Chats","^^^^^^^^^OBVH")


    }
    override fun getItemCount(): Int = values.size
}

* */