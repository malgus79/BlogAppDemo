package com.blogappdemo.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.blogappdemo.R
import com.blogappdemo.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        signUp()

    }

    private fun signUp() {
        binding.btnSignup.setOnClickListener {

            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()


            if (password != confirmPassword) {
                binding.editTextConfirmPassword.error = "Password does not match"
                binding.editTextPassword.error = "Password does not match"
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                binding.editTextUsername.error = "Username is empty"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.editTextEmail.error = "Email is empty"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.editTextPassword.error = "Password is empty"
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                binding.editTextConfirmPassword.error = "Confirm Password is empty"
                return@setOnClickListener
            }

            Log.d("signUpData", "data: $username $password $confirmPassword $email ")
        }
    }
}