package it.uniupo.progetto

class Consegna(
    val clientMail: String,
    val products: ArrayList<Prodotto>?,
    val posizione: String,
    val tipo_pagamento: String,
    var stato: Int,
    var orderId: String,
    var distanza: Double?,
    var rider : String ,
) {
   override fun toString(): String {
       return "$clientMail - $products - $posizione -$tipo_pagamento - $stato - $orderId - $distanza - $rider"
    }


}
