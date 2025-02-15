package com.winter.happyaging.ai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.winter.happyaging.R
import com.winter.happyaging.ResDTO.FallAnalysis
import com.winter.happyaging.ResDTO.ResponseDto
import com.winter.happyaging.ResDTO.RoomAIPrompt

class AnalysisAdapter(private var analysisList: List<RoomAIPrompt>) :
    RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder>() {

    class AnalysisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomCategoryTextView: TextView = itemView.findViewById(R.id.roomCategoryTextView)
        private val roomNumberTextView: TextView = itemView.findViewById(R.id.roomNumberTextView)
        private val roomNameTextView: TextView = itemView.findViewById(R.id.roomNameTextView)
        private val imageView1: ImageView = itemView.findViewById(R.id.imageView1)
        private val imageView2: ImageView = itemView.findViewById(R.id.imageView2)
        private val imageView3: ImageView = itemView.findViewById(R.id.imageView3)
        private val fallSummaryTextView: TextView = itemView.findViewById(R.id.fallSummaryTextView)
        private val fallRiskTextView: TextView = itemView.findViewById(R.id.fallRiskTextView)

        fun bind(item: RoomAIPrompt, position: Int) {
            val responseDto = item.responseDto ?: ResponseDto(
                imageDescription = "설명 없음",
                fallAnalysis = FallAnalysis("정보 없음", "정보 없음", "정보 없음"),
                fallSummaryDescription = "낙상 위험 요약 정보 없음",
                fallPreventionMeasures = emptyList()
            )

            fallSummaryTextView.text = responseDto.fallSummaryDescription
            roomCategoryTextView.text = item.roomCategory
            roomNumberTextView.text = (position + 1).toString()
            roomNameTextView.text = item.roomCategory + (position + 1)

            // 이미지 로딩
            val imageViews = listOf(imageView1, imageView2, imageView3)
            item.images.forEachIndexed { index, imageUrl ->
                if (index < imageViews.size) {
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.logo)
                        .into(imageViews[index])
                }
            }

            // 분석 요약
            fallSummaryTextView.text = item.responseDto.fallSummaryDescription

            // 위험 요소 리스트
            val fallAnalysis = item.responseDto.fallAnalysis
            val riskText = """
                1. ${fallAnalysis.obstacles}
                2. ${fallAnalysis.floorCondition}
                3. ${fallAnalysis.otherFactors}
            """.trimIndent()

            fallRiskTextView.text = riskText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_analysis, parent, false)
        return AnalysisViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnalysisViewHolder, position: Int) {
        holder.bind(analysisList[position], position)
    }

    override fun getItemCount(): Int = analysisList.size

    fun updateData(newList: List<RoomAIPrompt>) {
        analysisList = newList
        notifyDataSetChanged()
    }
}
