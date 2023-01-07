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
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.databinding.FragmentProfileBinding
import com.blogappdemo.presentation.auth.AuthViewModel
import com.blogappdemo.utils.Constants.DATA
import com.blogappdemo.utils.hide
import com.blogappdemo.utils.show
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<AuthViewModel>()
    private var bitmap: Bitmap? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent?>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        setupDataUpload()
        setupActivityResult()
        setupEditImageProfile()
        updateNewImageProfile()
        setupSignOut()

    }

    //carga de datos en fragment
    private fun setupDataUpload() {
        val user = FirebaseAuth.getInstance().currentUser
        Glide.with(this).load(user?.photoUrl).centerCrop().into(binding.imgProfile)
        with(binding) {
            imgProfile
            tvProfileName.text = user?.displayName
            tvProfileEmail.text = user?.email
        }
        Log.d("Usuario:", "fotourl: ${user?.photoUrl} , nombre: ${user?.displayName} ")
    }

    //solucion al onActivityResult @deprecated
    private fun setupActivityResult() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = it.data
                    val imageBitmap = data?.extras?.get(DATA)
                    binding.imgProfile.setImageBitmap(imageBitmap as Bitmap?)
                    bitmap = imageBitmap
                    with(binding) {
                        btnEditImage.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_light))
                        btnEditImage.setIconTintResource(R.color.white)
                        btnEditConfirm.show()
                    }
                }
            }
    }

    //editar imagen de perfil
    private fun setupEditImageProfile() {
        binding.btnEditImage.setOnClickListener {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                resultLauncher.launch(intent)
            } catch (e: ActivityNotFoundException) {
                val ly = binding.root
                Snackbar.make(ly, (R.string.application_not_found), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    //actualizar nueva imagen en firebase
    private fun updateNewImageProfile() {
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
    }

    //logOut
    private fun setupSignOut() {
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        findNavController().clearBackStack(R.id.homeScreenFragment)
    }
}