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
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File

class GestoreProdotto  : AppCompatActivity() {
    var imgUri: Uri? = null
    lateinit var img: ImageView
    lateinit var nome: String
    lateinit var codPhoto: String
    var REQUEST_CODE = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gestore_prodotto)
        codPhoto = (System.currentTimeMillis() / 1000).toString()
        val id = intent.getStringExtra("id-prodotto")!!
        lateinit var p: Prodotto
        val tvTitolo = findViewById<EditText>(R.id.titolo)
        val desc = findViewById<EditText>(R.id.desc)
        val prezzo = findViewById<EditText>(R.id.prezzo)
        val qta = findViewById<EditText>(R.id.qta)
        img = findViewById(R.id.img)
        val salva = findViewById<Button>(R.id.save_edit)
        val cancella = findViewById<Button>(R.id.delete)
        cancella.setOnClickListener {
            eliminaProdotto(id)
        }
        getProdottoFromDB(id.toInt(), object : MyCallback {
            override fun onCallback(value: Prodotto) {
                p = value
                tvTitolo.setText(value.titolo)
                desc.setText(value.desc)
                prezzo.setText(value.prezzo)
                qta.setText(value.qta.toString())
                Picasso.get().load(value.img).into(img)
                nome = value.titolo

            }
        })

        salva.setOnClickListener {
            if (p.titolo != tvTitolo.text.toString()) p.titolo = tvTitolo.text.toString()
            if (p.desc != desc.text.toString()) p.desc = desc.text.toString()
            if (p.prezzo != prezzo.text.toString()) p.prezzo = prezzo.text.toString()
            if (p.qta.toString() != qta.text.toString()) p.qta = qta.text.toString().toInt()

            if (imgUri != null){
                  p.img = imgUri.toString()
                updatePhotoProdotto(object : MyCallbackProd {
                    override fun onCallback(newP: Prodotto) {
                        Log.d("aggiorna", "newp = ${newP.img}, \n p = ${p.img}")
                        Log.d("photo", "$p")
                        updateProdotto(p)

                    }
                }, p)
                Log.d("photo","Return = $img")
            }
            updateProdotto(p)
            startActivity(Intent(applicationContext, GestoreActivity::class.java))
        }

        img.setOnClickListener {
            chooseMethod()
        }
    }


    private fun updateProdotto(p: Prodotto) {

        val db = FirebaseFirestore.getInstance()
        val entry = hashMapOf<String, Any?>(
                "img" to p.img,
                "id" to p.id,
                "qta" to p.qta,
                "titolo" to p.titolo,
                "prezzo" to p.prezzo,
                "desc" to p.desc
        )
            db.collection("products").document(p.id.toString()).set(entry, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("photo", "Prodotto modificato correttamente ")
                    }
                    .addOnFailureListener { e -> Log.w("---", "Error adding document", e) }

    }
    private fun updatePhotoProdotto(myCallback: MyCallbackProd, p: Prodotto) {
        val imageFileName = "products/$codPhoto"
        val storage = FirebaseStorage.getInstance().reference
        val newphoto = storage.child(imageFileName)
        newphoto.putFile(imgUri!!)
                .addOnSuccessListener {
                    Log.d("photo", "up -> $it \n")
                    val downloadTask = storage.child(imageFileName).downloadUrl
                    Log.d("photo", "downloadTask $downloadTask")
                    downloadTask.addOnSuccessListener {
                        Log.d("photo", "dtask ${storage.child(imageFileName).downloadUrl}")
                        Log.d("photo", "downloadurl $it")
                        p.img = it.toString()
                        myCallback.onCallback(p)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Errore caricamento foto", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

        }
    }

    private fun eliminaProdotto(id: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("products").document(id).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Prodotto eliminato correttamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, GestoreActivity::class.java))

                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
    }

    private fun chooseMethod() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Scegli come proseguire")
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_camera, null)
        builder.setCancelable(true)
        builder.setView(dialogView)

        val camera = dialogView.findViewById<ImageView>(R.id.camera)
        val gallery = dialogView.findViewById<ImageView>(R.id.gallery)

        val choose = builder.create()
        choose.show()

        camera.setOnClickListener {
            checkPermissionForCamera()
            choose.cancel()
        }
        gallery.setOnClickListener {
            checkPermissionForImage()
            choose.cancel()
        }

    }

    private fun takePhoto() {
        val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (camIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(camIntent, REQUEST_CODE)
        }
    }

    private fun choosePhoto() {
        val galIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(galIntent, REQUEST_CODE)
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    && (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                REQUEST_CODE = 1001
                requestPermissions(permission, REQUEST_CODE) // scelti da me
                requestPermissions(permissionCoarse, 1002) // scelti da me
                checkPermissionForImage()
            } else{
                choosePhoto()
            }
        }
    }

    private fun checkPermissionForCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                    && (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
                val permission = arrayOf(Manifest.permission.CAMERA)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                REQUEST_CODE = 1003
                requestPermissions(permission, REQUEST_CODE) // scelti da me
                requestPermissions(permissionCoarse, 1002) // scelti da me
                checkPermissionForCamera()
            }else{
                takePhoto()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1001) {
            Log.d("photo", "Scelgo foto")
            imgUri = data?.data
            if (imgUri != null) {
                img.setImageURI(imgUri)
                var storage = FirebaseStorage.getInstance().reference.child("products/$codPhoto")
                storage.putFile(imgUri!!).addOnSuccessListener {
                    Toast.makeText(this, "Foto caricata correttamente", Toast.LENGTH_SHORT).show()
                }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Errore caricamento foto", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
            }

        }
        if (resultCode == RESULT_OK && requestCode == 1003) {
            Log.d("photo", "Scatto foto")
            var bundle = data!!.extras
            var imgbit = bundle!!.get("data") as Bitmap

            val file = File(cacheDir, nome) //Get Access to a local file.
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
            var storage = FirebaseStorage.getInstance().reference.child("products/$codPhoto")
            storage.putFile(URI).addOnSuccessListener {
                Toast.makeText(this, "Foto caricata correttamente", Toast.LENGTH_SHORT).show()
            }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Errore caricamento foto", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }

        }
    }


    interface MyCallbackProd {
        fun onCallback(newP: Prodotto)
    }
    interface MyCallback {
        fun onCallback(value: Prodotto)
    }

    fun getProdottoFromDB(id: Int, myCallback: MyCallback) {

        val db = FirebaseFirestore.getInstance()
        db.collection("products")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("pprod", "id vale ${document.get("id").toString()} cerco $id")
                        if (document.get("id").toString() == id.toString()) {
                            var p = Prodotto(document.getLong("id")!!.toInt(), document.get("img")!!.toString(), document.get("titolo").toString(), document.get("desc").toString(), document.get("prezzo").toString(), document.getLong("qta")!!.toInt())
                            Log.d("pprod", "Prodotto vale $p")
                            myCallback.onCallback(p)
                        }

                    }
                }
                .addOnFailureListener { e -> Log.w("---", "Error getting document - GET PRODOTTO FROM DB", e) }
    }
}

