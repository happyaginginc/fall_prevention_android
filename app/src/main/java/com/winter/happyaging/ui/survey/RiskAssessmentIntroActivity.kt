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

        // 헤더 타이틀 설정
        binding.header.tvHeader.text = "낙상 위험등급 측정"
        // 뒤로가기 버튼 동작
        binding.header.btnBack.setOnClickListener {
            finish()
        }

        tokenManager = TokenManager(this)
        val seniorId = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getLong("seniorId", -1L)

        // TODO: 만약 토큰이 필요하면 토큰 검사. 없으면 로그인 유도 등
        // val accessToken = tokenManager.getAccessToken()

        // "위험등급 측정 시작하기" 버튼
        binding.btnStartSurvey.setOnClickListener {
            // QuestionRepository 에서 질문 목록을 캐싱하도록 먼저 호출한 뒤
            // 실제 설문 화면으로 이동
            QuestionRepository.getQuestionListOnce(this) {
                // 캐싱 완료 후 설문 화면 진입
                val intent = Intent(this, RiskAssessmentQuestionActivity::class.java)
                startActivity(intent)
            }
        }
    }
}