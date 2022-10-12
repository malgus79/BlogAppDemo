package com.blogappdemo.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.core.hide
import com.blogappdemo.core.show
import com.blogappdemo.data.remote.home.HomeScreenDataSource
import com.blogappdemo.databinding.FragmentHomeScreenBinding
import com.blogappdemo.domain.home.HomeScreenRepoImpl
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


        //obtener data de <post>
        viewModel.fetchLatestPosts().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    if(result.data.isEmpty()) {
                        binding.emptyContainer.show()
                        return@Observer
                    }else{
                        binding.emptyContainer.hide()
                    }
                    binding.rvHome.adapter = HomeScreenAdapter(result.data)
                }
                is Result.Failure -> {
                    binding.progressBar.hide()
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