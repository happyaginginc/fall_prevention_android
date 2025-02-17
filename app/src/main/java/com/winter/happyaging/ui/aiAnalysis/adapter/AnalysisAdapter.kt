package com.winter.happyaging.ui.aiAnalysis.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.RoomAIPrompt

class AnalysisAdapter(private var analysisList: List<RoomAIPrompt>) :
    RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder>() {

    class AnalysisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomCategoryTextView: TextView = itemView.findViewById(R.id.roomCategoryTextView)
        val roomNumberTextView: TextView = itemView.findViewById(R.id.roomNumberTextView)
        val roomNameTextView: TextView = itemView.findViewById(R.id.roomNameTextView)
        val imageView1: ImageView = itemView.findViewById(R.id.imageView1)
        val imageView2: ImageView = itemView.findViewById(R.id.imageView2)
        val imageView3: ImageView = itemView.findViewById(R.id.imageView3)
        val fallSummaryTextView: TextView = itemView.findViewById(R.id.fallSummaryTextView)
        val fallRiskTextView: TextView = itemView.findViewById(R.id.fallRiskTextView)

        fun bind(room: RoomAIPrompt) {
            roomCategoryTextView.text = room.roomCategory
            roomNumberTextView.text = room.roomAIPromptId.toString() // `roomNumber`가 없어서 ID로 대체
            roomNameTextView.text = "분석 ID: ${room.roomAIPromptId}"

            // `responseDto.fallSummaryDescription`을 사용
            fallSummaryTextView.text = room.responseDto.fallSummaryDescription
            fallRiskTextView.text = """
                장애물: ${room.responseDto.fallAnalysis.obstacles}
                바닥 상태: ${room.responseDto.fallAnalysis.floorCondition}
                기타 요인: ${room.responseDto.fallAnalysis.otherFactors}
            """.trimIndent()

            Log.d("AnalysisAdapter", "🔍 받은 이미지 URL 리스트: ${room.images}")

            // Glide로 이미지 로드
            val imageUrls = room.images // `images` 리스트 사용
            val placeholderImage = R.drawable.logo

            if (imageUrls.isNotEmpty()) {
                Glide.with(itemView.context).load(imageUrls.getOrNull(0)).placeholder(placeholderImage).into(imageView1)
                Glide.with(itemView.context).load(imageUrls.getOrNull(1)).placeholder(placeholderImage).into(imageView2)
                Glide.with(itemView.context).load(imageUrls.getOrNull(2)).placeholder(placeholderImage).into(imageView3)
            } else {
                imageView1.setImageResource(placeholderImage)
                imageView2.setImageResource(placeholderImage)
                imageView3.setImageResource(placeholderImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_analysis, parent, false)
        return AnalysisViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnalysisViewHolder, position: Int) {
        holder.bind(analysisList[position])
    }

    override fun getItemCount(): Int = analysisList.size

    fun updateData(newList: List<RoomAIPrompt>) {
        analysisList = newList
        notifyDataSetChanged()
    }
}
