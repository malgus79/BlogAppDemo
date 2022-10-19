package com.blogappdemo.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.core.hide
import com.blogappdemo.core.show
import com.blogappdemo.data.model.Post
import com.blogappdemo.data.remote.home.HomeScreenDataSource
import com.blogappdemo.databinding.FragmentHomeScreenBinding
import com.blogappdemo.domain.home.HomeScreenRepoImpl
import com.blogappdemo.presentation.home.HomeScreenViewModel
import com.blogappdemo.presentation.home.HomeScreenViewModelFactory
import com.blogappdemo.ui.main.adapter.HomeScreenAdapter
import com.blogappdemo.ui.main.adapter.OnPostClickListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeScreenFragment : Fragment(R.layout.fragment_home_screen), OnPostClickListener {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel by viewModels<HomeScreenViewModel> {
        HomeScreenViewModelFactory(HomeScreenRepoImpl(
            HomeScreenDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
        callback.isEnabled

        //obtener data de <post> segun el lifecycle con stateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                //obtener data de <post>
                viewModel.latestPosts.collectLatest { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.show()
                        }

                        is Result.Success -> {
                            binding.progressBar.hide()
                            if (result.data.isEmpty()) {
                                binding.emptyContainer.show()
                                return@collectLatest
                            } else {
                                binding.emptyContainer.hide()
                            }
                            binding.rvHome.adapter =
                                HomeScreenAdapter(result.data, this@HomeScreenFragment)
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
                }
            }
        }
/*
        //obtener data de <post>
        viewModel.fetchLatestPosts().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    if (result.data.isEmpty()) {
                        binding.emptyContainer.show()
                        return@Observer
                    } else {
                        binding.emptyContainer.hide()
                    }
                    binding.rvHome.adapter = HomeScreenAdapter(result.data, this)
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

 */
    }

    //al hacer click en like
    override fun onLikeButtonClick(post: Post, liked: Boolean) {
        viewModel.registerLikeButtonState(post.id, liked).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    Log.d("Like Transaction", "in progress...")
                }

                is Result.Success -> {
                    Log.d("Like Transaction", "Success")
                }

                is Result.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Ocurrio un error: ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    //al hacer click en share
    override fun onShareButtonClick(post: Post, shared: Boolean) {
        viewModel.registerShareButtonState(post.id, shared).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    Log.d("Share Transaction", "in progress...")
                }

                is Result.Success -> {
                    Log.d("Share Transaction", "Success")
                }

                is Result.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Ocurrio un error: ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}