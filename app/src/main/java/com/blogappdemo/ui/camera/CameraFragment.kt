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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.data.remote.camera.CameraDataSource
import com.blogappdemo.databinding.FragmentCameraBinding
import com.blogappdemo.domain.camera.CameraRepoImpl
import com.blogappdemo.presentation.camera.CameraViewModel
import com.blogappdemo.presentation.camera.CameraViewModelFactory
import com.blogappdemo.utils.Constants.DATA
import com.blogappdemo.utils.hideKeyboard
import com.google.android.material.snackbar.Snackbar

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
                val imageBitmap = it.data?.extras?.get(DATA) as Bitmap
                binding.ivPostImage.setImageBitmap(imageBitmap)
                bitmap = imageBitmap
            }
        }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                photoSelectedUri = it.data?.data
                binding.ivPostImage.setImageURI(photoSelectedUri)
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)

        openCamera()

        with(binding) {
            btnTakePhoto.setOnClickListener { takePhoto() }
            btnOpenGallery.setOnClickListener { openGallery() }
        }


        binding.cvUploadPhoto.setOnClickListener { it ->
            with(binding) {
                ivUpload.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_700))
                tvUpload.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_700))
            }

            //subir foto desde camara
            bitmap?.let {
                viewModel.uploadPhotoCamera(it, binding.etDescription.text.toString().trim())
                    .observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                Toast.makeText(requireContext(),
                                    (R.string.uploading_photo),
                                    Toast.LENGTH_SHORT).show()
                            }
                            is Result.Success -> {
                                //findNavController().navigate(R.id.action_cameraFragment_to_homeScreenFragment)
                                findNavController().popBackStack()
                                binding.etDescription.setText("")
                            }
                            is Result.Failure -> {
                                showResultFailure()
                            }
                        }
                    }
            }

            //subir foto desde galeria
            photoSelectedUri?.let {
                viewModel.uploadPhotoGallery(it, binding.etDescription.text.toString().trim())
                    .observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                Toast.makeText(requireContext(),
                                    (R.string.uploading_photo),
                                    Toast.LENGTH_SHORT).show()
                            }
                            is Result.Success -> {
                                //findNavController().navigate(R.id.action_cameraFragment_to_homeScreenFragment)
                                findNavController().popBackStack()
                                binding.etDescription.setText("")
                            }
                            is Result.Failure -> {
                                showResultFailure()
                            }
                        }
                    }
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
                (R.string.cant_open_gallery),
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
                (R.string.camera_app_not_found),
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
        binding.etDescription.setText("")
        super.onPause()
    }

    //snackbar transaction failure
    private fun showResultFailure() {
        val ly = binding.root
        Snackbar.make(ly, (R.string.error_occurred), Snackbar.LENGTH_LONG).show()
    }

}