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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.databinding.FragmentCameraBinding
import com.blogappdemo.presentation.camera.CameraViewModel
import com.blogappdemo.utils.Constants.DATA
import com.blogappdemo.utils.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding

    private var bitmap: Bitmap? = null
    private var photoSelectedUri: Uri? = null

    private lateinit var photoResult: ActivityResultLauncher<Intent?>
    private lateinit var galleryResult: ActivityResultLauncher<Intent?>

    private val viewModel by viewModels<CameraViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        binding.cvUploadPhoto.isEnabled = false

        setupPhotoActivityResult()
        setupGalleryActivityResult()
        openCamera()
        setupButtonUpLoad()
        setupOnClickActionButtons()

    }

    private fun setupPhotoActivityResult() {
        photoResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val imageBitmap = it.data?.extras?.get(DATA) as Bitmap
                    binding.ivPostImage.setImageBitmap(imageBitmap)
                    bitmap = imageBitmap
                    binding.cvUploadPhoto.isEnabled = true
                } else {
                    binding.cvUploadPhoto.isEnabled = false
                }
            }
    }

    private fun setupGalleryActivityResult() {
        galleryResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    photoSelectedUri = it.data?.data
                    binding.ivPostImage.setImageURI(photoSelectedUri)
                    binding.cvUploadPhoto.isEnabled = true
                } else {
                    binding.cvUploadPhoto.isEnabled = false
                }
            }
    }

    //abrir la camara
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            photoResult.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                (R.string.cant_open_gallery),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupButtonUpLoad() {
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
                                Toast.makeText(
                                    requireContext(), (R.string.uploading_photo), Toast.LENGTH_SHORT)
                                    .show()
                            }
                            is Result.Success -> {
                                clearFields()
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
                                Toast.makeText(
                                    requireContext(), (R.string.uploading_photo), Toast.LENGTH_SHORT)
                                    .show()
                            }
                            is Result.Success -> {
                                clearFields()
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

    private fun clearFields() {
        with(binding) {
            ivPostImage.setImageResource(R.drawable.ic_add_a_photo)
            etDescription.setText("")
            ivUpload.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_light))
            tvUpload.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_light))
            cvUploadPhoto.isEnabled = false
        }
    }

    private fun setupOnClickActionButtons() {
        with(binding) {
            btnTakePhoto.setOnClickListener { takePhoto() }
            btnOpenGallery.setOnClickListener { openGallery() }
        }
    }

    //tomar foto
    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            photoResult.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(),
                (R.string.camera_app_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    //abrir la galeria
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            galleryResult.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(),
                (R.string.gallery_app_error), Toast.LENGTH_SHORT).show()
        }
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

    override fun onDestroy() {
        super.onDestroy()
        findNavController().clearBackStack(R.id.homeScreenFragment)
    }
}