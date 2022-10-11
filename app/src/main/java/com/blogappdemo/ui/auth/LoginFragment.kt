package com.blogappdemo.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.data.remote.auth.LoginDataSource
import com.blogappdemo.databinding.FragmentLoginBinding
import com.blogappdemo.domain.auth.LoginRepoImpl
import com.blogappdemo.presentation.auth.LoginScreenViewModel
import com.blogappdemo.presentation.auth.LoginScreenViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel by viewModels<LoginScreenViewModel> {
        LoginScreenViewModelFactory(LoginRepoImpl(
            LoginDataSource()))
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
            findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment)
        }
    }

    //obtener email y pass y hacer login
    private fun doLogin() {
        binding.btnSignin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            validateCredentials(email, password)
            signIn(email, password)
        }
    }

    //navegar al fragment de registro
    private fun goToSignUpPage() {
        binding.txtSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    //validacion de email y pass
    private fun validateCredentials(email: String, password: String) {
        if (email.isEmpty()) {
            binding.editTextEmail.error = "E-mail is empty"
            return
        }
        if (password.isEmpty()) {
            binding.editTextPassword.error = "Password is empty"
            return
        }
    }

    //logear usuario con firebase
    private fun signIn(email: String, password: String) {
        viewModel.signIn(email, password).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.btnSignin.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(
                        requireContext(),
                        "Welcome ${result.data?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment)
                }
                is Result.Failure -> {
                    binding.progressBar.isVisible = false
                    binding.btnSignin.isEnabled = true
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