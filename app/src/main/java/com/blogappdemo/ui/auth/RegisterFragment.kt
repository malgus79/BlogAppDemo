package com.blogappdemo.ui.auth

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.data.remote.auth.AuthDataSource
import com.blogappdemo.databinding.FragmentRegisterBinding
import com.blogappdemo.domain.auth.AuthRepoImpl
import com.blogappdemo.presentation.auth.AuthViewModel
import com.blogappdemo.presentation.auth.AuthViewModelFactory

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(AuthRepoImpl(
            AuthDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        signUp()

    }

    //metodo para registrarse
    private fun signUp() {
        binding.btnSignup.setOnClickListener {

            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()


            if (validateUserData(password,
                    confirmPassword,
                    username,
                    email)
            ) return@setOnClickListener

            createUser(email, password, username)
        }
    }

    //crear usuario
    private fun createUser(email: String, password: String, username: String) {
        viewModel.signUp(email, password, username).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.btnSignup.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.isVisible = false
                    findNavController().navigate(R.id.action_registerFragment_to_homeScreenFragment)
                }
                is Result.Failure -> {
                    binding.progressBar.isVisible = false
                    binding.btnSignup.isEnabled = true
                }
            }
        })
    }

    //validacion de campos
    private fun validateUserData(
        password: String,
        confirmPassword: String,
        username: String,
        email: String,
    ): Boolean {
        if (password != confirmPassword) {
            binding.editTextConfirmPassword.error = "Password does not match"
            binding.editTextPassword.error = "Password does not match"
            return true
        }

        if (username.isEmpty()) {
            binding.editTextUsername.error = "Username is empty"
            return true
        }

        if (email.isEmpty()) {
            binding.editTextEmail.error = "Email is empty"
            return true
        }

        if (password.isEmpty()) {
            binding.editTextPassword.error = "Password is empty"
            return true
        }

        if (confirmPassword.isEmpty()) {
            binding.editTextConfirmPassword.error = "Confirm Password is empty"
            return true
        }
        return false
    }
}