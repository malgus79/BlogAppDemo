package com.blogappdemo.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.data.model.Post
import com.blogappdemo.databinding.FragmentHomeScreenBinding
import com.blogappdemo.presentation.home.HomeScreenViewModel
import com.blogappdemo.ui.main.adapter.HomeScreenAdapter
import com.blogappdemo.ui.main.adapter.OnPostClickListener
import com.blogappdemo.utils.hide
import com.blogappdemo.utils.show
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeScreenFragment : Fragment(R.layout.fragment_home_screen), OnPostClickListener {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel by viewModels<HomeScreenViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
        callback.isEnabled

        val result = listOf<Post>()
        initRecyclerView(result)
        configSwipe()

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
                            initRecyclerView(result.data)
                        }

                        is Result.Failure -> {
                            binding.progressBar.hide()
                            showResultFailure()
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

    //recyclerView
    private fun initRecyclerView(result: List<Post>) {
        binding.rvHome.adapter =
            HomeScreenAdapter(result, this@HomeScreenFragment)
    }

    //config del swipe
    private fun configSwipe() {
        binding.swipe.setColorSchemeResources(R.color.blue3, R.color.blue2)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.grey_light)

        binding.swipe.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipe.isRefreshing = false
            }, 2000)
        }
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
                    showResultFailure()
                }
            }
        }
    }

    //al hacer click en compartir
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
                    showResultFailure()
                }
            }
        }
    }

    //al hacer click en comentarios
    override fun onCommentButtonClick(post: Post, commented: Boolean) {
        viewModel.registerCommentButtonState(post.id, commented).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    Log.d("Comment Transaction", "in progress...")
                }
                is Result.Success -> {
                    Log.d("Comment Transaction", "Success")
                    val ly = binding.root
                    Snackbar.make(ly, (R.string.post_message_comment_success), Snackbar.LENGTH_LONG).show()
                }
                is Result.Failure -> {
                    showResultFailure()
                }
            }
        }
    }

    //snackbar transaction failure
    private fun showResultFailure() {
        val ly = binding.root
        Snackbar.make(ly, (R.string.error_occurred), Snackbar.LENGTH_LONG).show()
    }
}