package it.uniupo.progetto

import java.io.Serializable

class Contatto(val mail : String, val nome: String, val cognome: String, val tipo: String) {
    override fun toString(): String {
        return "$mail - $nome - $cognome -$tipo"
    }
}

/*


class Contatto(val nome: String, var ora: String, val anteprima: String, val notifiche: String, val tipo: String) {
    override fun toString(): String {
        return "$nome - $notifiche - $ora - $anteprima - $tipo"
    }
}

*/
