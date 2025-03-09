package com.winter.happyaging.ui.aiAnalysis.result.adapter

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.RoomAIPrompt

class AnalysisAdapter(private var analysisList: List<RoomAIPrompt>) :
    RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder>() {

    companion object {
        private const val BASE_IMAGE_URL = "https://api.happy-aging.co.kr/storage/images/"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_analysis, parent, false)
        return AnalysisViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
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

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(room: RoomAIPrompt) {
            try {
                roomNameTextView.text = room.roomName ?: "알 수 없음"
                val response = room.responseDto
                fallSummaryTextView.text = response?.fallSummaryDescription ?: "정보 없음"

                val fallAnalysis = response?.fallAnalysis
                if (fallAnalysis != null) {
                    val riskDetails = """
                        <b>바닥 상태:</b> ${fallAnalysis.floorCondition}<br><br>
                        <b>장애물:</b> ${fallAnalysis.obstacles}<br><br>
                        <b>기타 요인:</b> ${fallAnalysis.otherFactors}
                    """.trimIndent()
                    fallRiskTextView.text = Html.fromHtml(riskDetails, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    fallRiskTextView.text = "분석 결과 없음"
                }

                val fullUrls = room.images.map { "$BASE_IMAGE_URL$it" }
                imagesRecyclerView.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                imagesRecyclerView.adapter = ImageListAdapter(fullUrls)
            } catch (e: Exception) {
                roomNameTextView.text = "데이터 로드 실패"
                fallSummaryTextView.text = ""
                fallRiskTextView.text = ""
            }
        }
    }
}