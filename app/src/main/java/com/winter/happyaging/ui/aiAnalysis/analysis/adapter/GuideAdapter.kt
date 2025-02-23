package com.winter.happyaging.ui.aiAnalysis.analysis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.GuideData
import com.winter.happyaging.databinding.ItemGuideBinding

class GuideAdapter(
    private val roomIndex: Int,
    private val guides: List<GuideData>,
    private val onAddImageClick: (roomPosition: Int, guidePosition: Int) -> Unit,
    private val onDeleteImageClick: (roomPosition: Int, guidePosition: Int, imagePosition: Int) -> Unit
) : RecyclerView.Adapter<GuideAdapter.GuideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val binding = ItemGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(guides[position], position)
    }

    override fun getItemCount(): Int = guides.size

    inner class GuideViewHolder(private val binding: ItemGuideBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(guideData: GuideData, guideIndex: Int) {
            binding.tvGuide.text = guideData.guideText

            binding.rvGuideImages.layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = RoomImageAdapter(
                imageList = guideData.images,
                baseImageUrl = binding.root.context.getString(R.string.base_image_url),
                onAddClick = {
                    onAddImageClick(roomIndex, guideIndex)
                },
                onDeleteClick = { imagePos ->
                    onDeleteImageClick(roomIndex, guideIndex, imagePos)
                }
            )
            binding.rvGuideImages.adapter = adapter
        }
    }
}