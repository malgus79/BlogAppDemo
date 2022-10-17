package com.blogappdemo.ui.auth

import android.app.Activity
import android.app.AlertDialog
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
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.data.remote.auth.AuthDataSource
import com.blogappdemo.databinding.FragmentSetupProfileBinding
import com.blogappdemo.domain.auth.AuthRepoImpl
import com.blogappdemo.presentation.auth.AuthViewModel
import com.blogappdemo.presentation.auth.AuthViewModelFactory

class SetupProfileFragment : Fragment(R.layout.fragment_setup_profile) {

    private lateinit var binding: FragmentSetupProfileBinding
    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(AuthRepoImpl(
            AuthDataSource()))
    }
    private var bitmap: Bitmap? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent?>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupProfileBinding.bind(view)

        val btnProfileImage = binding.ivProfileImage
        btnProfileImage.setOnClickListener {
            dispatchTakePictureIntent()
        }

        //solucion al onActivityResult @deprecated
        resultLauncher = registerForActivityResult(
            ActivityResultContracts
                .StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.ivProfileImage.setImageBitmap(imageBitmap)
                bitmap = imageBitmap
            }
        }

        //crear profile
        binding.btnCreateProfile.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val alertDialog =
                AlertDialog.Builder(requireContext()).setTitle("Uploading photo...").create()
            bitmap?.let {
                if (username.isNotEmpty()) {
                    viewModel.updateUserProfile(imageBitmap = it, username = username)
                        .observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    alertDialog.show()
                                }
                                is Result.Success -> {
                                    alertDialog.dismiss()
                                    findNavController().navigate(R.id.action_setupProfileFragment_to_homeScreenFragment)
                                }
                                is Result.Failure -> {
                                    alertDialog.dismiss()
                                }
                            }
                        }
                }
            }
        }
    }

    //abrir la camara
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            resultLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(),
                "No se encontro app para abir la camara",
                Toast.LENGTH_SHORT).show()
        }
    }
}