package it.uniupo.progetto.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fb = FirebaseAuth.getInstance()
        val user = fb.currentUser?.email
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        Log.d("login","${activity!!.getSharedPreferences("login",0).getString("login","")}")
        if (view is RecyclerView) {
            with(view) {
                var array = ArrayList<Azione>()
                array.add(Azione("Dati personali",0))

                array.add(Azione("Logout",3))
                array.sortBy { it.id }
                getUserType(user, object : LoginActivity.FirestoreCallback {
                    override fun onCallback(type: String) {
                        if(type=="Cliente") {
                            array.add(Azione("La mia posizione", 1))
                            array.add(Azione("I miei ordini", 2))
                        }else if(type=="Rider"){
                            array.add(Azione("I miei ordini", 2))
                        }
                    }
                })
                adapter = ProfileActionsAdapter(array)
            }
        }
        return view
    }

    private fun getUserType(user: String?, fc: LoginActivity.FirestoreCallback){
        val db = FirebaseFirestore.getInstance()
        var t = "null"
        db.collection("users").document(user!!)
                .get()
                .addOnSuccessListener { result ->
                    if(!result.getString("type").isNullOrBlank())
                        t = result.getString("type")!!
                    fc.onCallback(t)
                }
    }


    class Azione(val nome : String, val id : Int){
        override fun toString():String{
            return "Azione eseguita : $nome"
        }
    }

}