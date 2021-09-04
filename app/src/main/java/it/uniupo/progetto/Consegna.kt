package it.uniupo.progetto

class Consegna(
    val clientMail: String,
    val products: ArrayList<Prodotto>?,
    val posizione: String,
    val tipo_pagamento: String,
<<<<<<< HEAD
    var stato: String,
=======
    var stato: Int,
//    var statoPagamento: Int,
>>>>>>> 16caefea8b77745f20c2fdf9d1fb1f94764b2d4f
    var orderId: String,
    var distanza: Double,
    var rider : String ,
) {
   override fun toString(): String {
       return "$clientMail - $products - $posizione -$tipo_pagamento - $stato - $orderId - $distanza - $rider"
    }


}
