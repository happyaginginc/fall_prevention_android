package com.winter.happyaging.ui.aiAnalysis.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.RoomAIPrompt

class AnalysisAdapter(private var analysisList: List<RoomAIPrompt>) :
    RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder>() {

    companion object {
        private const val TAG = "AnalysisAdapter"
        private const val BASE_IMAGE_URL = "https://api.happy-aging.co.kr/storage/images/"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_analysis, parent, false)
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

    class AnalysisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomNameTextView: TextView = itemView.findViewById(R.id.roomNameTextView)
        private val imagesRecyclerView: RecyclerView = itemView.findViewById(R.id.imagesRecyclerView)
        private val fallSummaryTextView: TextView = itemView.findViewById(R.id.fallSummaryTextView)
        private val fallRiskTextView: TextView = itemView.findViewById(R.id.fallRiskTextView)

        fun bind(room: RoomAIPrompt) {
            roomNameTextView.text = room.roomName

            fallSummaryTextView.text = room.responseDto.fallSummaryDescription
            fallRiskTextView.text = """
                <바닥 상태>
                ${room.responseDto.fallAnalysis.floorCondition}

                <장애물>
                ${room.responseDto.fallAnalysis.obstacles}

                <기타 요인>
                ${room.responseDto.fallAnalysis.otherFactors}
            """.trimIndent()

            Log.d(TAG, "🔍 받은 이미지 이름 리스트: ${room.images}")

            // Base URL과 합쳐 최종 이미지 URL 리스트 생성
            val fullUrls = room.images.map { imageName -> "$BASE_IMAGE_URL$imageName" }

            // RecyclerView를 가로로 스크롤 가능하도록 설정
            imagesRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

            // ImageListAdapter를 이용해 가로 스크롤 이미지들 표시
            imagesRecyclerView.adapter = ImageListAdapter(fullUrls)
        }
    }
}