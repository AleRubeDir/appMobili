package it.uniupo.progetto


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.progetto.fragments.ProfileFragment

 class ProfileActionsAdapter (
    private val array: ArrayList<ProfileFragment.Azione>
) : RecyclerView.Adapter<ProfileActionsAdapter.ViewHolder>() {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view = LayoutInflater.from(parent.context)
             .inflate(R.layout.action, parent, false)
         val id  = view.findViewById<TextView>(R.id.id)
         val row = view.findViewById<RelativeLayout>(R.id.action_row)
         val nome  = view.findViewById<TextView>(R.id.nome)


         row.setOnClickListener{
             Log.d("prof","Cliccato ${id.text} - ${nome.text} ")
             if(id.text=="0"){
                //dati personali
                 val intent = Intent(view.context, DatiPersonali::class.java)
                 /*intent.putExtra("id-prodotto", id.text )*/
                 view.context.startActivity(intent)
             }
             if(id.text=="1"){
                 val intent = Intent(view.context, ClientMappa::class.java)
                 /*intent.putExtra("id-prodotto", id.text )*/
                 view.context.startActivity(intent)
             }
             if(id.text=="2"){
                //i miei ordini
             }
             if(id.text=="3"){
                 FirebaseAuth.getInstance().signOut()
                 view.context.startActivity(Intent(view.context, MainActivity::class.java))
                 Toast.makeText(view.context,"Logout effettuato", Toast.LENGTH_SHORT).show()
             }
         }
         return ViewHolder(view)
     }
    override fun getItemCount(): Int = array.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = array[position]
        holder.text.text = item.nome
        holder.id.text = item.id.toString()
    }

     inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
         var text: TextView = view.findViewById(R.id.nome)
         var id : TextView = view.findViewById(R.id.id)
     }
}