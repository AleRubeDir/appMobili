package it.uniupo.progetto.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.progetto.HomeActivity.Companion.array
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R


class ShopFragment : Fragment() {
    private var columnCount = 1
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                ItemFragment.getAllProducts((object : ItemFragment.Companion.MyCallback {
                    override fun onCallback(value: List<Prodotto>) {
                        adapter = MyShopRecycleViewAdapter(array)
                    }
                }))
            }
        }
        return view
    }

}
