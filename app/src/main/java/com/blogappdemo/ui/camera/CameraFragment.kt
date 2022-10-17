package com.blogappdemo.ui.camera

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.core.hideKeyboard
import com.blogappdemo.data.remote.camera.CameraDataSource
import com.blogappdemo.databinding.FragmentCameraBinding
import com.blogappdemo.domain.camera.CameraRepoImpl
import com.blogappdemo.presentation.camera.CameraViewModel
import com.blogappdemo.presentation.camera.CameraViewModelFactory

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding
    private var bitmap: Bitmap? = null
    private var photoSelectedUri: Uri? = null
    private val viewModel by viewModels<CameraViewModel> {
        CameraViewModelFactory(CameraRepoImpl(
            CameraDataSource()))
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageBitmap = it.data?.extras?.get("data") as Bitmap
                binding.postImage.setImageBitmap(imageBitmap)
                bitmap = imageBitmap
            }
        }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                photoSelectedUri = it.data?.data
                binding.postImage.setImageURI(photoSelectedUri)
            }
        }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)

        openCamera()

        binding.btnTakePhoto.setOnClickListener { takePhoto() }
        binding.btnOpenGallery.setOnClickListener { openGallery() }

        //subir foto desde camara
        binding.btnUploadPhoto.setOnClickListener {
            bitmap?.let {
                viewModel.uploadPhotoCamera(it, binding.etxtDescription.text.toString().trim())
                    .observe(viewLifecycleOwner, Observer { result ->
                        when (result) {
                            is Result.Loading -> {
                                Toast.makeText(requireContext(),
                                    "Uploading photo...",
                                    Toast.LENGTH_SHORT).show()
                            }
                            is Result.Success -> {
                                //findNavController().navigate(R.id.action_cameraFragment_to_homeScreenFragment)
                                findNavController().popBackStack()
                                binding.etxtDescription.setText("")
                            }
                            is Result.Failure -> {
                                Toast.makeText(requireContext(),
                                    "Error ${result.exception}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }

            //subir foto desde galeria
            photoSelectedUri?.let {
                viewModel.uploadPhotoGallery(it, binding.etxtDescription.text.toString().trim())
                    .observe(viewLifecycleOwner, Observer { result ->
                        when (result) {
                            is Result.Loading -> {
                                Toast.makeText(requireContext(),
                                    "Uploading photo...",
                                    Toast.LENGTH_SHORT).show()
                            }
                            is Result.Success -> {
                                //findNavController().navigate(R.id.action_cameraFragment_to_homeScreenFragment)
                                findNavController().popBackStack()
                                binding.etxtDescription.setText("")
                            }
                            is Result.Failure -> {
                                Toast.makeText(requireContext(),
                                    "Error ${result.exception}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
            it.hideKeyboard()
        }
    }

    //abrir la camara
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            resultLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(),
                "No se encontro app para abir la camara",
                Toast.LENGTH_SHORT).show()
        }
    }

    //tomar foto
    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            resultLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(),
                "No se encontro app para abir la camara",
                Toast.LENGTH_SHORT).show()
        }
    }

    //abrir la galeria
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResult.launch(intent)
    }

    //setear vacio el campo descripcion al salir del fragment
    override fun onPause() {
        binding.etxtDescription.setText("")
        super.onPause()
    }


}