package com.blogappdemo.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.*
import com.blogappdemo.data.remote.auth.AuthDataSource
import com.blogappdemo.databinding.FragmentRegisterBinding
import com.blogappdemo.domain.auth.AuthRepoImpl
import com.blogappdemo.presentation.auth.AuthViewModel
import com.blogappdemo.presentation.auth.AuthViewModelFactory
import com.blogappdemo.utils.*

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

            val username = binding.tietUsername.text.toString().trim()
            val password = binding.tietPassword.text.toString().trim()
            val confirmPassword = binding.tietConfirmPassword.text.toString().trim()
            val email = binding.tietEmail.text.toString().trim()

            if (validateUserData(password,
                    confirmPassword,
                    username,
                    email)
            ) return@setOnClickListener

            createUser(email, password, username)
            it.hideKeyboard()

            Log.d("signUpData", "data: $username $password $confirmPassword $email ")
        }
    }

    //crear usuario
    private fun createUser(email: String, password: String, username: String) {
        viewModel.signUp(email, password, username).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    with(binding) {
                        progressBar.show()
                        btnSignup.disable()
                    }
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    findNavController().navigate(R.id.action_registerFragment_to_setupProfileFragment)
                }
                is Result.Failure -> {
                    with(binding) {
                        progressBar.hide()
                        btnSignup.enable()
                    }
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
            with(binding) {
                tietConfirmPassword.error = getString(R.string.password_does_not_match)
                tietPassword.error = getString(R.string.password_does_not_match)
            }
            return true
        }

        if (username.isEmpty()) {
            binding.tietUsername.error = getString(R.string.username_is_empty)
            return true
        }

        if (email.isEmpty()) {
            binding.tietEmail.error = getString(R.string.email_is_empty)
            return true
        }

        if (password.isEmpty()) {
            binding.tietPassword.error = getString(R.string.password_is_empty)
            return true
        }

        if (confirmPassword.isEmpty()) {
            binding.tietConfirmPassword.error = getString(R.string.confirm_password_is_empty)
            return true
        }
        return false
    }
}