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

    class AnalysisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomNameTextView: TextView = itemView.findViewById(R.id.roomNameTextView)

        // 기존에 imageView1, imageView2, imageView3 제거
        val imagesRecyclerView: RecyclerView = itemView.findViewById(R.id.imagesRecyclerView)

        val fallSummaryTextView: TextView = itemView.findViewById(R.id.fallSummaryTextView)
        val fallRiskTextView: TextView = itemView.findViewById(R.id.fallRiskTextView)

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

            Log.d("AnalysisAdapter", "🔍 받은 이미지 URL 리스트: ${room.images}")

            // Base URL 붙여서 실제 URL 리스트 만들기
            val baseUrl = "https://api.happy-aging.co.kr/storage/images/"
            val fullUrls = room.images.map { imageName ->
                "$baseUrl$imageName"
            }

            // 가로로 스크롤할 수 있게 LayoutManager 설정
            imagesRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

            // ImageListAdapter를 이용해 풀 URL 리스트 표시
            val imageListAdapter = ImageListAdapter(fullUrls)
            imagesRecyclerView.adapter = imageListAdapter
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
