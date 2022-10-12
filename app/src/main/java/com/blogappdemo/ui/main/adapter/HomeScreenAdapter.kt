package com.blogappdemo.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blogappdemo.core.BaseViewHolder
import com.blogappdemo.core.TimeUtils
import com.blogappdemo.data.model.Post
import com.blogappdemo.databinding.PostItemViewBinding
import com.bumptech.glide.Glide

class HomeScreenAdapter(private val postList: List<Post>) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    //inflar el xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding =
            PostItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeScreenViewHolder(itemBinding, parent.context)
    }

    //inflar a cada item con la data
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is HomeScreenViewHolder -> holder.bind(postList[position])
        }
    }

    //tamaño de la list
    override fun getItemCount(): Int = postList.size

    //bindear la data
    private inner class HomeScreenViewHolder(
        val binding: PostItemViewBinding,
        val context: Context,
    ) : BaseViewHolder<Post>(binding.root) {
        override fun bind(item: Post) {
            Glide.with(context).load(item.post_image).centerCrop().into(binding.postImage)
            Glide.with(context).load(item.profile_picture).centerCrop().into(binding.profilePicture)
            binding.profileName.text = item.profile_name
            if (item.post_description.isEmpty()) {
                binding.postDescription.isVisible = false
            } else {
                binding.postDescription.text = item.post_description
            }


            val createdAt = (item.created_at?.time?.div(1000L))?.let {
                TimeUtils.getTimeAgo(it.toInt())
            }
            binding.postTimestamp.text = createdAt
        }
    }
}