package it.uniupo.progetto.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.ClienteActivity.Companion.array
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R
import it.uniupo.progetto.recyclerViewAdapter.*

class ShopFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.myList)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        ItemFragment.getAllProducts((object : ItemFragment.Companion.MyCallback {
            override fun onCallback(value: List<Prodotto>) {
                recyclerView.adapter = MyShopRecycleViewAdapter(value as ArrayList<Prodotto>)
            }
        }))

        return view
    }

}
