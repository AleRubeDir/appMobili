package it.uniupo.progetto

import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import java.util.*

class Messaggio(val inviato: Int, var ora: Timestamp, val testo: String) : Comparable<Any> {
    override fun toString(): String {
        return testo
    }


    override fun compareTo(other: Any): Int {
        other as Messaggio
        if(ora.toDate().after(other.ora.toDate())) return 1
        else if(ora.toDate().before(other.ora.toDate())) return -1
        return 0
    }
}