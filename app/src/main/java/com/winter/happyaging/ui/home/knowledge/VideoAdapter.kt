package com.winter.happyaging.ui.home.knowledge

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.winter.happyaging.data.youtube.model.YoutubeVideo
import com.winter.happyaging.databinding.ItemVideoBinding

class VideoAdapter(private var videoList: List<YoutubeVideo>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(video: YoutubeVideo) {
            binding.tvVideoTitle.text = video.title
            Glide.with(binding.ivThumbnail.context)
                .load(video.thumbnailUrl)
                .into(binding.ivThumbnail)
            binding.root.setOnClickListener {
                try {
                    val context = binding.root.context
                    val videoId = video.videoId
                    if (videoId.isNotEmpty()) {
                        val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
                        val packageManager: PackageManager = context.packageManager
                        if (youtubeIntent.resolveActivity(packageManager) != null) {
                            context.startActivity(youtubeIntent)
                            return@setOnClickListener
                        }
                    }
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId")))
                } catch (e: Exception) {
                    Toast.makeText(binding.root.context, "영상 재생 중 오류 발생", Toast.LENGTH_SHORT).show()
                }
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

    fun updateData(newList: List<YoutubeVideo>) {
        videoList = newList
        notifyDataSetChanged()
    }
}