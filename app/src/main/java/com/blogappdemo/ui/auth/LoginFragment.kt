package com.blogappdemo.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.*
import com.blogappdemo.data.remote.auth.AuthDataSource
import com.blogappdemo.databinding.FragmentLoginBinding
import com.blogappdemo.domain.auth.AuthRepoImpl
import com.blogappdemo.presentation.auth.AuthViewModel
import com.blogappdemo.presentation.auth.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(AuthRepoImpl(
            AuthDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        isUserLoggedIn()
        doLogin()
        goToSignUpPage()
    }

    //chequear si el usuario esta logeado y dirigirlo al home o al registro
    private fun isUserLoggedIn() {
        firebaseAuth.currentUser?.let { user ->
            if (user.displayName.isNullOrEmpty()) {
                findNavController().navigate(R.id.action_loginFragment_to_setupProfileFragment)
            } else {
                findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment)
            }
        }
    }

    //obtener email y pass y hacer login
    private fun doLogin() {
        binding.btnSignin.setOnClickListener {
            it.hideKeyboard()
            val email = binding.tietEmail.text.toString().trim()
            val password = binding.tietPassword.text.toString().trim()
            validateCredentials(email, password)
            signIn(email, password)
        }
    }

    //navegar al fragment de registro
    private fun goToSignUpPage() {
        binding.tvSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    //validacion de email y pass
    private fun validateCredentials(email: String, password: String) {
        if (email.isEmpty()) {
            binding.tietEmail.error = "E-mail is empty"
            return
        }
        if (password.isEmpty()) {
            binding.tietPassword.error = "Password is empty"
            return
        }
    }

    //logear usuario con firebase
    private fun signIn(email: String, password: String) {
        viewModel.signIn(email, password).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    with(binding) {
                        progressBar.show()
                        btnSignin.disable()
                    }
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    Toast.makeText(
                        requireContext(),
                        "Welcome ${result.data?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (result.data?.displayName.isNullOrEmpty()) {
                        findNavController().navigate(R.id.action_loginFragment_to_setupProfileFragment)
                    } else {
                        findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment)
                    }
                }
                is Result.Failure -> {
                    with(binding) {
                        progressBar.hide()
                        btnSignin.enable()
                    }
                    Toast.makeText(
                        requireContext(),
                        "Error: ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}