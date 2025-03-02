package com.winter.happyaging.ui.aiAnalysis.result.adapter

import android.text.Html
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

            val response = room.responseDto
            fallSummaryTextView.text = response.fallSummaryDescription

            val fallAnalysis = response.fallAnalysis
            val riskDetails = """
            <b>바닥 상태:</b> ${fallAnalysis.floorCondition}<br><br>
            <b>장애물:</b> ${fallAnalysis.obstacles}<br><br>
            <b>기타 요인:</b> ${fallAnalysis.otherFactors}
        """.trimIndent()
            fallRiskTextView.text = Html.fromHtml(riskDetails, Html.FROM_HTML_MODE_LEGACY)

            val fullUrls = room.images.map { imageName -> "$BASE_IMAGE_URL$imageName" }
            imagesRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            imagesRecyclerView.adapter = ImageListAdapter(fullUrls)
        }
    }
}