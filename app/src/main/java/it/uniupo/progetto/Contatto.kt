package it.uniupo.progetto

class Contatto(val mail : String, val nome: String, val cognome: String) {
    override fun toString(): String {
        return "$mail - $nome - $cognome "
    }
}

