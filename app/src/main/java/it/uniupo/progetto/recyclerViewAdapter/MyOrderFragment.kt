/*
package it.uniupo.progetto.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.progetto.LoginActivity
import it.uniupo.progetto.ProfileActionsAdapter
import it.uniupo.progetto.R

class MyOrderFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val fb = FirebaseAuth.getInstance()

        val user = fb.currentUser?.email

        val view = inflater.inflate(R.layout.fragment_my_orders, container, false)
        Log.d("login", "---- ${requireActivity().getSharedPreferences("login", 0).getString("login", "")}")
        val recyclerView = view.findViewById<RecyclerView>(R.id.profile_actions)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        var array = ArrayList<ProfileFragment.Azione>()

        array.add(ProfileFragment.Azione("Traccia il tuo ordine", 0))
        array.add(ProfileFragment.Azione("Chat con rider", 1))
        array.add(ProfileFragment.Azione("Richiama ordine", 2))
        array.add(ProfileFragment.Azione("Storico degli ordini", 3))
        array.sortBy { it.id }

        recyclerView.adapter = MyOrderActionsAdapter(array)

        return view

    }

}*/
