package com.blogappdemo.ui.camera

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.databinding.FragmentCameraBinding

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding
    private val REQUEST_IMAGE_CAPTURE = 2
    private var bitmap: Bitmap? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent?>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)

        val btnUploadPicture = binding.btnUploadPhoto
        btnUploadPicture.setOnClickListener {
            dispatchTakePictureIntent()
        }

        resultLauncher = registerForActivityResult(
            ActivityResultContracts
                .StartActivityForResult()
        ){
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.imgAddPhoto.setImageBitmap(imageBitmap)
                bitmap = imageBitmap
            }
        }
    }

    //abrir la camara
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            resultLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No se encontro app para abir la camara", Toast.LENGTH_SHORT).show()
        }
    }

//    //deprecado
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            binding.imgAddPhoto.setImageBitmap(imageBitmap)
//            bitmap = imageBitmap
//        }
//    }
}