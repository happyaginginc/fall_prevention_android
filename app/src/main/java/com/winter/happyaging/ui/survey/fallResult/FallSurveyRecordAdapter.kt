package com.winter.happyaging.ui.survey.fallResult

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R
import com.winter.happyaging.data.survey.model.SurveyResultData
import com.winter.happyaging.databinding.ItemFallSurveyRecordBinding

class FallSurveyRecordAdapter(
    private val onItemClick: (SurveyResultData) -> Unit
) : RecyclerView.Adapter<FallSurveyRecordAdapter.FallSurveyRecordViewHolder>() {

    private var surveyList: List<SurveyResultData> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FallSurveyRecordViewHolder {
        val binding = ItemFallSurveyRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FallSurveyRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FallSurveyRecordViewHolder, position: Int) {
        holder.bind(surveyList[position], onItemClick)
    }

    override fun getItemCount(): Int = surveyList.size

    fun updateData(newList: List<SurveyResultData>) {
        surveyList = newList
        notifyDataSetChanged()
    }

    inner class FallSurveyRecordViewHolder(private val binding: ItemFallSurveyRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(survey: SurveyResultData, onItemClick: (SurveyResultData) -> Unit) {
            // 위험도 수치와 카테고리 텍스트 설정
            binding.tvRiskNumber.text = "${survey.riskLevel}등급"
            val riskCategory = when (survey.riskLevel) {
                1 -> "위험"
                2 -> "보통"
                3 -> "안전"
                else -> "보통"
            }
            binding.tvRiskCategory.text = riskCategory

            // 위험 등급에 따라 배경색 변경 (1: 위험, 2: 보통, 3: 안전)
            val colorResId = when (survey.riskLevel) {
                1 -> R.color.risk_level_high
                2 -> R.color.risk_level_medium
                3 -> R.color.risk_level_low
                else -> R.color.risk_level_medium
            }
            binding.llRiskContainer.backgroundTintList =
                ContextCompat.getColorStateList(binding.llRiskContainer.context, colorResId)

            // 요약 텍스트 설정 (XML에서 패딩 처리됨)
            binding.tvSummary.text = survey.summary ?: "설문 요약이 없습니다."

            binding.root.setOnClickListener {
                onItemClick(survey)
            }
        }
    }
}
