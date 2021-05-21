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
import java.io.ByteArrayOutputStream
import java.io.File

class GestoreProdotto  : AppCompatActivity() {
    var imgUri : Uri? = null
    lateinit var img : ImageView
    lateinit var nome : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gestore_prodotto)
        val id = intent.getStringExtra("id-prodotto")!!
        var p: Prodotto
        val tvTitolo = findViewById<EditText>(R.id.titolo)
        val desc = findViewById<EditText>(R.id.desc)
        val prezzo = findViewById<EditText>(R.id.prezzo)
        val qta = findViewById<EditText>(R.id.qta)
        img = findViewById(R.id.img)
        val salva = findViewById<Button>(R.id.save_edit)



        getProdottoFromDB(id.toInt(), object: MyCallback {
            override fun onCallback(p: Prodotto) {
                tvTitolo.setText(p.titolo)
                desc.setText(p.desc)
                prezzo.setText(p.prezzo)
                qta.setText(p.qta.toString())
                img.setImageURI(p.img.toUri())
                nome = p.titolo
                salva.setOnClickListener{
                    updatePhotoDB(imgUri!!,p)
                }
            }
        })
        img.setOnClickListener{
            chooseMethod()
        }
      /*  val storage = FirebaseStorage.getInstance().reference.child("products/$nome")
        val localfile = File.createTempFile("temp","jpg")
        storage.getFile(localfile).addOnSuccessListener {

        }*/
    }
    private fun chooseMethod(){
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Scegli come proseguire")
        var inflater = layoutInflater
        var dialogView = inflater.inflate(R.layout.dialog_camera,null)
        builder.setCancelable(true)
        builder.setView(dialogView)

        var camera = dialogView.findViewById<ImageView>(R.id.camera)
        var gallery = dialogView.findViewById<ImageView>(R.id.gallery)


        var choose = builder.create()
        choose.show()


        camera.setOnClickListener{
            checkPermissionForCamera()
                choose.cancel()
            /*if(checkAndRequestPermission()){
                takePhoto()
                choose.cancel()
            }*/
        }
        gallery.setOnClickListener{
            checkPermissionForImage()
            choose.cancel()
        }

    }
    private fun takePhoto(){
        val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(camIntent.resolveActivity(packageManager)!=null){
            startActivityForResult(camIntent,1003)
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
        Log.d("photo", "Scelgo foto")
        imgUri = data?.data
        if (imgUri != null) {
            img.setImageURI(imgUri)
            var storage = FirebaseStorage.getInstance().reference.child("products/$nome")
            storage.putFile(imgUri!!).addOnSuccessListener {
                Toast.makeText(this, "Foto caricata correttamente", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Errore caricamento foto", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        }
        Log.d("photo", "data.data vale $imgUri Scelgo foto")
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
        var storage = FirebaseStorage.getInstance().reference.child("products/$nome")
        storage.putFile(URI).addOnSuccessListener {
            Toast.makeText(this, "Foto caricata correttamente FS", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errore caricamento foto FS", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

    }
}
    private fun updatePhotoDB(imgUri: Uri, p : Prodotto) {
        val fs = FirebaseStorage.getInstance().reference
        val imageFileName ="products/${p.titolo}"
        val downloadTask = fs.child(imageFileName).downloadUrl
        downloadTask.addOnSuccessListener {
            Log.d("photo","downloadurl $it")
        }


    }

    interface MyCallback{
        fun onCallback(value: Prodotto)
    }

    fun getProdottoFromDB(id: Int,myCallback: MyCallback){

        val db = FirebaseFirestore.getInstance()
        db.collection("products")
                .get()
                .addOnSuccessListener { result->
                    for (document in result) {
                        Log.d("pprod", "id vale ${document.get("id").toString()} cerco $id")
                        if (document.get("id").toString() == id.toString()) {
                            var p= Prodotto(document.getLong("id")!!.toInt(), document.get("img")!!.toString(), document.get("titolo").toString(), document.get("desc").toString(), document.get("prezzo").toString(), document.getLong("qta")!!.toInt())
                            Log.d("pprod", "Prodotto vale $p")
                            myCallback.onCallback(p)
                        }

                    }
                }
                .addOnFailureListener{ e -> Log.w("---", "Error getting document - GET PRODOTTO FROM DB", e)}
    }

}