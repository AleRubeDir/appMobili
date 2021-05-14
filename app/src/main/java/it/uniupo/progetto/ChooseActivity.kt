package it.uniupo.progetto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.uniupo.progetto.fragments.Gestore
import kotlin.system.exitProcess

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
                //Toast.makeText(this, "Seleziona una categoria", Toast.LENGTH_SHORT).show()
            } else {
                //salva dati su DB
                var n = name.text.toString()
                var s = surname.text.toString()
                var type = getUserType(rg.checkedRadioButtonId)
                addToFirestore(n, s, type, mail!!)
                if (rg.checkedRadioButtonId == R.id.scelta_customer) {
                    val intent = Intent(this, ClientMappa::class.java)
                    intent.putExtra("mail", mail!!)
                    startActivity(intent)
                }

                if (rg.checkedRadioButtonId == R.id.scelta_rider) {
                    val intent = Intent(this, ClientMappa::class.java)
                    intent.putExtra("mail", mail!!)
                    startActivity(intent)
                }
            }
            if (rg.checkedRadioButtonId == R.id.scelta_gestore) {
                val intent = Intent(this, GestoreActivity::class.java)
                intent.putExtra("mail", mail!!)
                startActivity(intent)
            }
        }


        //startActivity(Intent(this, HomeActivity::class.java))
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
        var db: FirebaseFirestore = FirebaseFirestore.getInstance()
        var auth = FirebaseAuth.getInstance()
        val entry = hashMapOf<String, Any?>(
                "name" to name,
                "surname" to surname,
                "type" to type,
        )
        Log.d("google", "mail in choose vale $mail")
        db.collection("users").document(mail!!)
                .set(entry, SetOptions.merge())
                .addOnSuccessListener { documentReference ->
                    Log.d("user", "Aggiornati campi name = $name e surname = $surname")
                }
                .addOnFailureListener { e -> Log.w("---", "Error adding document", e) }
    }
}