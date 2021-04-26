package it.uniupo.progetto.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.uniupo.progetto.Prodotto
import it.uniupo.progetto.R


class ShopFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
/*        val p1 = Prodotto(R.mipmap.ic_launcher,"Pasta","Pasta barilla integrale","2,99€",99)
        val p2 = Prodotto(R.mipmap.ic_launcher,"Pasta","Pasta barilla 00","1,99€",99)
        array = arrayOf(p1,p2)
        val myList = view?.findViewById<ListView>(R.id.scrollView)
        if (myList != null) {
            myList.adapter = MyArrayAdapter(HomeActivity(), R.layout.row, array)
            Log.d("***","adapter :   ${myList.adapter} \n myList :    $myList ")
        }*/

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop, container, false)
    }
}
