package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ChooseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_activity)

        val end = findViewById<Button>(R.id.end)
        val rg = findViewById<RadioGroup>(R.id.type_group)
        val name = findViewById<EditText>(R.id.name)
        val surname = findViewById<EditText>(R.id.surname)
        val mail = intent.getStringExtra("mail")
        end.setOnClickListener {
            Toast.makeText(this, "${rg.checkedRadioButtonId}", Toast.LENGTH_SHORT).show()
            if (rg.isSelected) {
                Toast.makeText(this, "${rg.checkedRadioButtonId}", Toast.LENGTH_SHORT).show()
            } else {
                //salva dati su DB
                val n = name.text.toString()
                val s = surname.text.toString()
                val type = getUserType(rg.checkedRadioButtonId)
                addToFirestore(n, s, type, mail!!)
                if (rg.checkedRadioButtonId == R.id.scelta_customer) {
                    val intent = Intent(this, ClientMappa::class.java)
                    intent.putExtra("mail", mail)
                    startActivity(intent)
                }

                if (rg.checkedRadioButtonId == R.id.scelta_rider) {
                    val intent = Intent(this, RiderActivity::class.java)
                    intent.putExtra("mail", mail)
                    startActivity(intent)
                }
            }
            if (rg.checkedRadioButtonId == R.id.scelta_gestore) {
                val intent = Intent(this, GestoreActivity::class.java)
                intent.putExtra("mail", mail)
                startActivity(intent)
            }
        }
    }


    private fun getUserType(id: Int): String {
        if (id == R.id.scelta_customer)
            return "Cliente"
        if (id == R.id.scelta_gestore)
            return "Gestore"
        if (id == R.id.scelta_rider)
            return "Rider"
        return "err"
    }

    private fun addToFirestore(name: String, surname: String, type: String, mail: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String, Any?>(
                "name" to name,
                "surname" to surname,
                "type" to type,
                "mail" to mail,
        )
        Log.d("google", "mail in choose vale $mail")
        db.collection("users").document(mail)
                .set(entry, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("user", "Aggiornati campi name = $name e surname = $surname")
                }
                .addOnFailureListener { e -> Log.w("---", "Error adding document", e) }
    }
}