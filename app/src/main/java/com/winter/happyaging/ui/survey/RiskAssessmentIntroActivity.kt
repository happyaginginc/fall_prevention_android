package com.winter.happyaging.ui.survey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.winter.happyaging.data.survey.repository.QuestionRepository
import com.winter.happyaging.databinding.ActivityRiskAssessmentIntroBinding
import com.winter.happyaging.network.TokenManager

class RiskAssessmentIntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiskAssessmentIntroBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiskAssessmentIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.tvHeader.text = "낙상 위험등급 측정"
        binding.header.btnBack.setOnClickListener { finish() }

        tokenManager = TokenManager(this)
        getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getLong("seniorId", -1L) // seniorId 사용 여부에 따라 추가 처리 가능

        binding.btnStartSurvey.setOnClickListener {
            QuestionRepository.getQuestionListOnce(this) {
                startActivity(Intent(this, RiskAssessmentQuestionActivity::class.java))
            }
        }
    }
}
