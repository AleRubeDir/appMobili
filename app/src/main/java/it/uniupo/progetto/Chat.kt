package it.uniupo.progetto

import it.uniupo.progetto.Contatto
import it.uniupo.progetto.Messaggio

class Chat (val contatto : Contatto, val messaggio: ArrayList<Messaggio>, var notifications : Int) {
    override fun toString(): String {
        return "Chat con $contatto - notifiche $notifications \n " +
                "[lista messaggi] " + messaggio + "\n----------------------------------------------------\n"
    }

}