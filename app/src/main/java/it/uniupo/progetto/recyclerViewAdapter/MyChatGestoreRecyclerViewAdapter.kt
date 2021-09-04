package it.uniupo.progetto.recyclerViewAdapter


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyChatGestoreRecyclerViewAdapter(
    private val chats: ArrayList<Chat>
) : RecyclerView.Adapter<MyChatGestoreRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_element, parent, false)
        val el = view.findViewById<RelativeLayout>(R.id.chat_element)
        val mail = view.findViewById<TextView>(R.id.mail)
        el.setOnClickListener{
            Toast.makeText(view.context,"${mail.text}",Toast.LENGTH_SHORT).show()
            val intent = Intent(view.context, ChatActivity::class.java)
            intent.putExtra("mail", mail.text)
            view.context.startActivity(intent)

        }
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mail : TextView = view.findViewById(R.id.mail)
        var nome: TextView = view.findViewById(R.id.name)
        var cognome: TextView = view.findViewById(R.id.surname)
        var ora : TextView = view.findViewById(R.id.ora)
        var anteprima : TextView = view.findViewById(R.id.anteprima)
        var notifiche : TextView = view.findViewById(R.id.notifiche)
        var data : TextView = view.findViewById(R.id.data)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = chats[position]
        Log.d("mymess","${item.contatto}")
        item.messaggio.sortBy{it.ora}
        var ora = item.messaggio.last().ora.toDate().hours.toString()
        var minuti = item.messaggio.last().ora.toDate().minutes.toString()
        if(minuti.length==1) minuti = "0"+ item.messaggio.last().ora.toDate().minutes.toString()

        holder.mail.text = item.contatto.mail
        holder.nome.text = item.contatto.nome
        holder.cognome.text = item.contatto.cognome
        holder.ora.text = "$ora:$minuti"
        Log.d("anteprima","ultimo messaggio => ${item.messaggio.last().testo} \n\n")
        holder.anteprima.text = item.messaggio.last().testo
        holder.data.text =convertLongToTime(item.messaggio.last().ora.seconds)
        if(item.notifications==0) holder.notifiche.visibility = View.INVISIBLE
        holder.notifiche.text = item.notifications.toString()
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time*1000)
        //  Log.d("mess","time vale $time date vale $date")
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }
}