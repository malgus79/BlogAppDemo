package com.blogappdemo.ui.camera

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.data.remote.camera.CameraDataSource
import com.blogappdemo.databinding.FragmentCameraBinding
import com.blogappdemo.domain.camera.CameraRepoImpl
import com.blogappdemo.presentation.camera.CameraViewModel
import com.blogappdemo.presentation.camera.CameraViewModelFactory

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding
    private var bitmap: Bitmap? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent?>
    private val viewModel by viewModels<CameraViewModel> {
        CameraViewModelFactory(CameraRepoImpl(
            CameraDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)

        binding.btnTakePhoto.setOnClickListener {
            openCamera()
        }

        //solucion al onActivityResult @deprecated
        resultLauncher = registerForActivityResult(
            ActivityResultContracts
                .StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageBitmap = it.data?.extras?.get("data") as Bitmap
                binding.postImage.setImageBitmap(imageBitmap)
                bitmap = imageBitmap
            }
        }

        binding.btnUploadPhoto.setOnClickListener {
            bitmap?.let {
                viewModel.uploadPhoto(it, binding.etxtDescription.text.toString().trim()).observe(viewLifecycleOwner, Observer { result ->
                     when (result) {
                         is Result.Loading -> {
                             Toast.makeText(requireContext(),"Uploading photo...", Toast.LENGTH_SHORT).show()
                         }
                         is Result.Success -> {
                             findNavController().navigate(R.id.action_cameraFragment_to_homeScreenFragment)
                         }
                         is Result.Failure -> {
                             Toast.makeText(requireContext(),"Error ${result.exception}", Toast.LENGTH_SHORT).show()
                         }
                     }
                })
            }
        }
    }

    //abrir la camara
    private fun openCamera() {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(),
                "No se encontro app para abir la camara",
                Toast.LENGTH_SHORT).show()
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