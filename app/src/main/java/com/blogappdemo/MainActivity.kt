package com.blogappdemo

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultLauncher = registerForActivityResult(
            ActivityResultContracts
                .StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                imageView.setImageBitmap(imageBitmap)
                uploadPicture(imageBitmap)
            }
        }

        imageView = findViewById(R.id.imageView)

        val btnTakePicture = findViewById<Button>(R.id.btn_take_picture)
        btnTakePicture.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    //Intent para lanzar camara en dispositivo
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            resultLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No se encontro app para tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    //subir foto al storage
    private fun uploadPicture(bitmap: Bitmap) {
        //crear una referencia en el storage
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("image.jpg")
        val baos = ByteArrayOutputStream()
        //comprimir la foto
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        //transformar la imagen en un ByteArray
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { exception ->
                    throw exception
                }
            }
            imageRef.downloadUrl
        //consumir el task
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                Log.d("Storage", "uploadPicture: $downloadUri")
            }
        }
    }
}
