package com.blogappdemo.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.utils.hide
import com.blogappdemo.utils.show
import com.blogappdemo.data.remote.auth.AuthDataSource
import com.blogappdemo.databinding.FragmentProfileBinding
import com.blogappdemo.domain.auth.AuthRepoImpl
import com.blogappdemo.presentation.auth.AuthViewModel
import com.blogappdemo.presentation.auth.AuthViewModelFactory
import com.blogappdemo.utils.Constants.DATA
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(AuthRepoImpl(
            AuthDataSource()))
    }
    private var bitmap: Bitmap? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent?>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        //carga de datos en fragment
        val user = FirebaseAuth.getInstance().currentUser
        Glide.with(this).load(user?.photoUrl).centerCrop().into(binding.imgProfile)
        with(binding) {
            imgProfile
            tvProfileName.text = user?.displayName
            tvProfileEmail.text = user?.email
        }
        Log.d("Usuario:", "fotourl: ${user?.photoUrl} , nombre: ${user?.displayName} ")

        //solucion al onActivityResult @deprecated
        resultLauncher = registerForActivityResult(
            ActivityResultContracts
                .StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                val imageBitmap = data?.extras?.get(DATA)
                binding.imgProfile.setImageBitmap(imageBitmap as Bitmap?)
                bitmap = imageBitmap
            }
        }

        //editar imagen de perfil
        binding.btnEditImage.setOnClickListener {
            editImageProfile()
            with(binding) {
                btnEditImage.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_light))
                btnEditImage.setIconTintResource(R.color.white)
                btnEditConfirm.show()
            }
        }

        //actualizar nueva imagen en firebase
        binding.btnEditConfirm.setOnClickListener {
            val username = binding.tvProfileName.text.toString().trim()
            val alertDialog =
                AlertDialog.Builder(requireContext()).setTitle(R.string.uploading_changes).create()
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

                                }
                                is Result.Failure -> {
                                    alertDialog.dismiss()
                                }
                            }
                        }
                }
            }
            with(binding) {
                btnEditImage.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
                btnEditImage.setIconTintResource(R.color.white)
                btnEditConfirm.hide()
            }
        }

        //logOut
        binding.btnLogout.setOnClickListener {
            signOut()
        }
    }

    //abrir camara
    private fun editImageProfile() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            resultLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            val ly = binding.root
            Snackbar.make(ly, (R.string.application_not_found), Snackbar.LENGTH_LONG).show()
        }
    }

    //logOut
    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        activity?.finish()
    }
}