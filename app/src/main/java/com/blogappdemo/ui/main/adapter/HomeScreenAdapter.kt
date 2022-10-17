package com.blogappdemo.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blogappdemo.R
import com.blogappdemo.core.BaseViewHolder
import com.blogappdemo.core.TimeUtils
import com.blogappdemo.core.hide
import com.blogappdemo.core.show
import com.blogappdemo.data.model.Post
import com.blogappdemo.databinding.PostItemViewBinding
import com.bumptech.glide.Glide

class HomeScreenAdapter(
    private val postList: List<Post>,
    private val onPostClickListener: OnPostClickListener,
) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    //estado de likes
    private var postClickListener: OnPostClickListener? = null
    init {
        postClickListener = onPostClickListener
    }

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
            setupProfileInfo(item)
            addPostTimeStamp(item)
            setupPostImage(item)
            setupPostDescription(item)
            tintHeartIcon(item)
            setupLikeCount(item)
            setLikeClickAction(item)
        }

        //foto de perfil y nombre del usuario
        private fun setupProfileInfo(post: Post) {
            Glide.with(context).load(post.poster?.profile_picture).centerCrop()
                .into(binding.profilePicture)
            binding.tvProfileName.text = post.poster?.username
        }

        //timestamp
        private fun addPostTimeStamp(post: Post) {
            val createdAt = (post.created_at?.time?.div(1000L))?.let {
                TimeUtils.getTimeAgo(it.toInt())
            }
            binding.tvPostTimestamp.text = createdAt
        }

        //imagen del post
        private fun setupPostImage(post: Post) {
            Glide.with(context).load(post.post_image).centerCrop().into(binding.ivPostImage)
        }

        //postDescription
        private fun setupPostDescription(post: Post) {
            if (post.post_description.isEmpty()) {
                binding.tvPostDescription.isVisible = false
            } else {
                binding.tvPostDescription.text = post.post_description
            }
        }

        //pintar el like (favourite)
        private fun tintHeartIcon(post: Post) {
            if (!post.liked) {
                binding.ivLikeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border))
                binding.ivLikeBtn.setColorFilter(ContextCompat.getColor(context, R.color.black))
            } else {
                binding.ivLikeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite))
                binding.ivLikeBtn.setColorFilter(ContextCompat.getColor(context, R.color.red_like))
            }
        }

        //contador de likes
        private fun setupLikeCount(post: Post) {
            if (post.likes > 0) {
                binding.tvLikeCount.show()
                binding.tvLikeCount.text = "${post.likes} likes"
            } else {
                binding.tvLikeCount.hide()
            }
        }

        //accion al click del like
        private fun setLikeClickAction(post: Post) {
            binding.ivLikeBtn.setOnClickListener {
                if(post.liked) post.apply { liked = false } else post.apply { liked = true }
                //pintar color segun estado
                tintHeartIcon(post)
                //enviar al fragment si fue linkeado y su estado en ese momento
                postClickListener?.onLikeButtonClick(post, post.liked)
            }
        }
    }
}