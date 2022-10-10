package com.blogappdemo.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blogappdemo.R
import com.blogappdemo.data.model.Post
import com.blogappdemo.databinding.FragmentHomeScreenBinding
import com.blogappdemo.ui.home.adapter.HomeScreenAdapter
import com.google.firebase.Timestamp

class HomeScreenFragment : Fragment(R.layout.fragment_home_screen) {

    private lateinit var binding: FragmentHomeScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)

        val postList = listOf(
            Post(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTux2cCcnReia24DwLgdWan_zuWYclkUm2P2w&usqp=CAU",
                "Cat",
                Timestamp.now(),
                "https://live.staticflickr.com/65535/48386006486_772affb4cf_b.jpg"),
            Post("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRslhCv1GRj75J1FyLhF3CQ8142I_WZcAYAnQ&usqp=CAU",
                "Face",
                Timestamp.now(),
                "https://upload.wikimedia.org/wikipedia/commons/a/af/User_profile_personal_details.png"),
            Post("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ1Clr2n4hdj_6T68FWG3lLOsz4uGigz_su7g&usqp=CAU",
                "Wolf",
                Timestamp.now(),
                "https://www.wncoutdoors.info/wp-content/uploads/2021/05/2021-05-15_grandfather-mountain-state-park_profile-trail-upper-section-steps.jpg")
        )
        binding.rvHome.adapter = HomeScreenAdapter(postList)
    }
}