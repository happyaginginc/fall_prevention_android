package com.winter.happyaging.ui.aiAnalysis.analysis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.winter.happyaging.R
import com.winter.happyaging.databinding.ItemRoomImageBinding

class RoomImageAdapter(
    private val imageList: MutableList<String>,
    private val baseImageUrl: String,
    private val onAddClick: () -> Unit,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_ADD = 2
    }

    override fun getItemViewType(position: Int): Int =
        if (position == imageList.size) VIEW_TYPE_ADD else VIEW_TYPE_IMAGE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemRoomImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if (viewType == VIEW_TYPE_IMAGE) ImageViewHolder(binding) else AddViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder && position < imageList.size) {
            holder.bind(imageList[position])
        } else if (holder is AddViewHolder) {
            holder.bind()
        }
    }

    inner class ImageViewHolder(private val binding: ItemRoomImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageName: String) {
            try {
                Glide.with(binding.imageView.context)
                    .load("$baseImageUrl$imageName")
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(binding.imageView)
            } catch (e: Exception) {
                binding.imageView.setImageResource(R.drawable.logo)
            }
            binding.btnDelete.setOnClickListener { onDeleteClick(adapterPosition) }
        }
    }

    inner class AddViewHolder(private val binding: ItemRoomImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.imageView.setImageResource(R.drawable.ic_add_image)
            binding.btnDelete.visibility = View.GONE
            binding.root.setOnClickListener { onAddClick() }
        }
    }
}