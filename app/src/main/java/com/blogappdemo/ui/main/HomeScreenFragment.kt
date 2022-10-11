package com.blogappdemo.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.blogappdemo.R
import com.blogappdemo.core.Resources
import com.blogappdemo.data.remote.HomeScreenDataSource
import com.blogappdemo.databinding.FragmentHomeScreenBinding
import com.blogappdemo.domain.HomeScreenRepoImpl
import com.blogappdemo.presentation.home.HomeScreenViewModel
import com.blogappdemo.presentation.home.HomeScreenViewModelFactory
import com.blogappdemo.ui.main.adapter.HomeScreenAdapter

class HomeScreenFragment : Fragment(R.layout.fragment_home_screen) {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel by viewModels<HomeScreenViewModel> {
        HomeScreenViewModelFactory(HomeScreenRepoImpl(
            HomeScreenDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)


        viewModel.fetchLatestPosts().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resources.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is Resources.Success -> {
                    binding.progressBar.isVisible = false
                    binding.rvHome.adapter = HomeScreenAdapter(result.data)
                }
                is Resources.Failure -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(
                        requireContext(),
                        "Ocurrio un error: ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}