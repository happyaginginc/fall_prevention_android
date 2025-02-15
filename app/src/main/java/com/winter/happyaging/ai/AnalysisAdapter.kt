package com.winter.happyaging.ai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.databinding.ItemAnalysisBinding
import com.winter.happyaging.ResDTO.RoomAIPrompt

class AnalysisAdapter(private var analysisList: List<RoomAIPrompt>) :
    RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder>() {

    class AnalysisViewHolder(private val binding: ItemAnalysisBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(roomPrompt: RoomAIPrompt) {
            binding.tvRoomCategory.text = "방 유형: ${roomPrompt.roomCategory}"
            binding.tvImageDescription.text = "이미지 설명: ${roomPrompt.responseDto.imageDescription}"
            binding.tvFallSummary.text = "낙상 분석: ${roomPrompt.responseDto.fallSummaryDescription}"
            binding.tvPreventionMeasures.text = "예방 조치: ${roomPrompt.responseDto.fallPreventionMeasures.joinToString("\n")}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val binding = ItemAnalysisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnalysisViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnalysisViewHolder, position: Int) {
        holder.bind(analysisList[position])
    }

    override fun getItemCount(): Int = analysisList.size

    fun updateData(newData: List<RoomAIPrompt>) {
        analysisList = newData
        notifyDataSetChanged()
    }
}
