package com.winter.happyaging.ui.home.knowledge

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.winter.happyaging.data.video.VideoItem
import com.winter.happyaging.databinding.ItemVideoBinding

class VideoAdapter(private var videoList: List<VideoItem>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    val currentList: List<VideoItem> get() = videoList

    inner class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(video: VideoItem) {
            binding.tvVideoTitle.text = video.title
            Glide.with(binding.ivThumbnail.context)
                .load(video.thumbnailUrl)
                .into(binding.ivThumbnail)
            binding.root.setOnClickListener {
                // 영상 클릭 시 YouTube 앱이나 브라우저에서 재생
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.videoUrl))
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int = videoList.size

    fun updateData(newList: List<VideoItem>) {
        videoList = newList
        notifyDataSetChanged()
    }
}