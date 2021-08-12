package it.uniupo.progetto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File

class AddProduct : AppCompatActivity() {
    lateinit var img : ImageView
    lateinit var imgUri : Uri
    lateinit var codphoto : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product)
        val tsLong = System.currentTimeMillis() / 1000
        codphoto = tsLong.toString()
        img = findViewById(R.id.img)
        val add = findViewById<Button>(R.id.add_product)

        add.setOnClickListener {
            val titolo = findViewById<EditText>(R.id.titolo).text.toString()
            val desc = findViewById<EditText>(R.id.desc).text.toString()
            val prezzo = findViewById<EditText>(R.id.prezzo).text.toString()
            var qta = findViewById<EditText>(R.id.qta).text.toString()
     
            getLastID(object : MyCallback {
                override fun onCallback(id: Int) {
                    if (imgUri.toString().isBlank()) {
                        mostraErrore("immagine")
                    } else if (titolo.isBlank()) {
                        mostraErrore("titolo")
                    } else if (desc.isBlank()) {
                        mostraErrore("descrizione")
                    } else if (prezzo.isBlank()) {
                        mostraErrore("prezzo")
                    } else if (qta.isBlank()) {
                        mostraErrore("quantità")
                    } else if (qta.toInt() < 1) {
                        mostraErrore("quantità")
                    } else {
                        val p = Prodotto(id+1, imgUri.toString(), titolo, desc, prezzo, qta.toInt())
                        Log.d("prodotto", "Creato prodotto $p")
                        caricaProdotto(p)
                        updatePhoto(p)
                        startActivity(Intent(applicationContext, GestoreActivity::class.java))

                    }
                }
            })
        }
        img.setOnClickListener{
            chooseMethod()
        }
    }
    private fun caricaProdotto(p: Prodotto) {
        val db = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String, Any?>(
                "id" to p.id,
                "titolo" to p.titolo,
                "desc" to p.desc,
                "img" to p.img,
                "qta" to p.qta,
                "prezzo" to p.prezzo,
        )
        db.collection("products").document(p.id.toString())
                .set(entry)
                .addOnSuccessListener {
                    Log.d("add", "Aggiunto prodotto $entry")
                }
                .addOnFailureListener{ e -> Log.w("---", "Errore aggiunta prodotto", e)}
    }
    private fun mostraErrore(campo: String){
        Toast.makeText(this, "Errore $campo", Toast.LENGTH_SHORT).show()
    }
    private fun chooseMethod(){
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Scegli come proseguire")
        var inflater = layoutInflater
        var dialogView = inflater.inflate(R.layout.dialog_camera, null)
        builder.setCancelable(true)
        builder.setView(dialogView)

        var camera = dialogView.findViewById<ImageView>(R.id.camera)
        var gallery = dialogView.findViewById<ImageView>(R.id.gallery)


        var choose = builder.create()
        choose.show()


        camera.setOnClickListener{
            checkPermissionForCamera()
            choose.cancel()
        }
        gallery.setOnClickListener{
            checkPermissionForImage()
            choose.cancel()
        }

    }
    private fun takePhoto(){
        val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(camIntent.resolveActivity(packageManager)!=null){
            startActivityForResult(camIntent, 1003)
        }
    }
    private fun choosePhoto(){
        val galIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(galIntent, 1001)
    }
    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    && (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(permission, 1001) // scelti da me
                requestPermissions(permissionCoarse, 1002) // scelti da me
            } else {
                choosePhoto()
            }
        }
    }
    private fun checkPermissionForCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                    && (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED))
            {
                val permission = arrayOf(Manifest.permission.CAMERA)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 1003) // scelti da me
                requestPermissions(permissionCoarse, 1002) // scelti da me
            } else {
                takePhoto()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("photo", "res = $resultCode req 0 $requestCode")
        if (resultCode == RESULT_OK && requestCode == 1001) {
            Log.d("add", "Scelgo foto")
            if (data != null) {
                imgUri = data.data!!
            }
            img.setImageURI(imgUri)
            val storage = FirebaseStorage.getInstance().reference.child("products/$codphoto")

            storage.putFile(imgUri!!).addOnSuccessListener {
                Toast.makeText(this, "Foto caricata correttamente", Toast.LENGTH_SHORT).show()
            }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Errore caricamento foto", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
            Log.d("add", "data.data vale $imgUri Scelgo foto")
        }
        if (resultCode == RESULT_OK && requestCode == 1003) {
            Log.d("add", "Scatto foto")
            var bundle = data!!.extras
            var imgbit = bundle!!.get("data") as Bitmap

            val file = File(cacheDir, codphoto) //Get Access to a local file.
            file.delete() // svuota
            file.createNewFile() //crea
            //scrive su cache
            val fileOutputStream = file.outputStream()
            val byteArrayOutputStream = ByteArrayOutputStream()
            imgbit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val bytearray = byteArrayOutputStream.toByteArray()
            fileOutputStream.write(bytearray)
            fileOutputStream.flush()
            fileOutputStream.close()
            byteArrayOutputStream.close()
            val URI = file.toUri()
            img.setImageBitmap(imgbit)
            var storage = FirebaseStorage.getInstance().reference.child("products/$codphoto")
            storage.putFile(URI).addOnSuccessListener {
                Toast.makeText(this, "Foto caricata correttamente FS", Toast.LENGTH_SHORT).show()
            }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Errore caricamento foto FS", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }

        }
    }
    private fun updatePhoto(p: Prodotto) {
        val fs = FirebaseStorage.getInstance().reference
        val imageFileName ="products/$codphoto"
        val downloadTask = fs.child(imageFileName).downloadUrl
        downloadTask.addOnSuccessListener {
            Log.d("add", "downloadurl $it")
            val entry = hashMapOf<String, Any?>(
                    "img" to it.toString(),
            )
            Log.d("add","entry vale = $entry")
             var fb = FirebaseFirestore.getInstance()
             fb.collection("products").document(p.id.toString()).set(entry, SetOptions.merge())
                 .addOnSuccessListener {
                     Toast.makeText(this, "Foto caricata correttamente FD", Toast.LENGTH_SHORT).show()
                 }
                 .addOnFailureListener{ e->
                     Toast.makeText(this, "Errore caricamento foto FD", Toast.LENGTH_SHORT).show()
                     e.printStackTrace()
                 }
        }


    }
    private fun getLastID(myCallback: MyCallback){
        var list = mutableListOf<Int>()
        var fb = FirebaseFirestore.getInstance()
        fb.collection("products").get()
                .addOnSuccessListener { result ->
                    for (doc in result) {
                        list.add(doc.id.toInt())
                    }
                myCallback.onCallback(list[list.size - 1])
                }
    }
    interface MyCallback{
        fun onCallback(id: Int)
    }
}