package com.winter.happyaging.ui.aiAnalysis.adapter

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
    // 콜백: 추가 버튼 클릭 시와 삭제 버튼 클릭 시
    private val onAddClick: () -> Unit,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 뷰타입 상수
    companion object {
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_ADD = 2
    }

    override fun getItemViewType(position: Int): Int {
        // 마지막 아이템은 항상 “추가하기” 버튼으로 표시
        return if (position == imageList.size) VIEW_TYPE_ADD else VIEW_TYPE_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemRoomImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if (viewType == VIEW_TYPE_IMAGE) {
            ImageViewHolder(binding)
        } else {
            AddViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = imageList.size + 1 // +1 for add button

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder) {
            holder.bind(imageList[position])
        } else if (holder is AddViewHolder) {
            holder.bind()
        }
    }

    inner class ImageViewHolder(private val binding: ItemRoomImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageName: String) {
            // 전체 URL 생성 (이미지 파일명 앞에 BASE_IMAGE_URL 붙임)
            val fullUrl = "$baseImageUrl$imageName"
            Glide.with(binding.imageView.context)
                .load(fullUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(binding.imageView)

            // 삭제 버튼 클릭
            binding.btnDelete.setOnClickListener {
                onDeleteClick(adapterPosition)
            }
        }
    }

    inner class AddViewHolder(private val binding: ItemRoomImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            // “추가” 아이콘 설정
            binding.imageView.setImageResource(R.drawable.ic_add_image)
            binding.btnDelete.visibility = View.GONE
            binding.root.setOnClickListener {
                onAddClick()
            }
        }
    }
}