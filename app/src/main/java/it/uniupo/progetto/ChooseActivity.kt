package it.uniupo.progetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class ChooseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_activity)

        val end = findViewById<Button>(R.id.end)
        val rg = findViewById<RadioGroup>(R.id.type_group)
        end.setOnClickListener{
            if(!rg.isSelected){
                Toast.makeText(this,"Seleziona una categoria",Toast.LENGTH_SHORT)
            }else{
                //salva dati su DB
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    private fun addToFirestore(name: String, surname: String, mail: String, type: String){
        var db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String,Any?>(
            "name" to name,
            "surname" to surname,
            "mail" to mail,
            "type" to type
        )
        db.collection("users")
            .add(entry)
            .addOnSuccessListener { documentReference -> Log.d("***","DocumentSnapshot add with ID ${documentReference}")
            }
            .addOnFailureListener{ e -> Log.w("---","Error adding document",e)}

    }
}