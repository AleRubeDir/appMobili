package it.uniupo.progetto

class Consegna(
    val clientMail: String,
    val products: ArrayList<Prodotto>?,
    val posizione: String,
    val tipo_pagamento: String,
    var stato: String,
    var orderId: String,
    var distanza: Double
) {
//    override fun toString(): String {
//        return "$mail - $nome - $cognome -$tipo"
//    }


}
