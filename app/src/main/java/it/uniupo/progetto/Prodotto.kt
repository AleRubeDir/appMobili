package it.uniupo.progetto

import java.io.Serializable

class Prodotto(val id: Int, var img: String, var titolo: String, var desc: String, var prezzo: String, var qta: Int) : Serializable {

    override fun toString(): String {
        return "$id - $titolo - $desc - $prezzo - $qta - $img"
    }
    companion object {

        fun getProdotto(id: Int): Prodotto? {
            for (p in ClienteActivity.array) {
                if (p.id == id)
                    return p
            }
            return null
        }

    }
}