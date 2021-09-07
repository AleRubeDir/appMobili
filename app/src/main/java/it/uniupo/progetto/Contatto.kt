package it.uniupo.progetto

class Contatto(val mail : String, val nome: String, val cognome: String, val tipo: String) {
    override fun toString(): String {
        return "$mail - $nome - $cognome -$tipo"
    }
}

