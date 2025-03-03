package com.winter.happyaging.ui.survey

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.data.survey.model.SurveyAnswerRequest
import com.winter.happyaging.data.survey.model.SurveyCreateRequest
import com.winter.happyaging.data.survey.model.SurveyCreateResponse
import com.winter.happyaging.data.survey.model.SurveyQuestionResponse
import com.winter.happyaging.data.survey.repository.QuestionRepository
import com.winter.happyaging.data.survey.service.SurveyService
import com.winter.happyaging.databinding.ActivityRiskAssessmentQuestionBinding
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.ui.survey.adapter.SurveyQuestionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiskAssessmentQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiskAssessmentQuestionBinding
    private lateinit var questionAdapter: SurveyQuestionAdapter
    private var questionList: List<SurveyQuestionResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiskAssessmentQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 헤더 설정
        binding.header.tvHeader.text = "낙상 위험등급 설문"
        binding.header.btnBack.setOnClickListener { finish() }

        // RecyclerView 초기 설정
        questionList = QuestionRepository.getCachedQuestionList()
        questionAdapter = SurveyQuestionAdapter(questionList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@RiskAssessmentQuestionActivity)
            adapter = questionAdapter
        }

        // "제출" 버튼
        binding.btnSubmit.setOnClickListener {
            submitSurveyAnswers()
        }
    }

    private fun submitSurveyAnswers() {
        // Adapter 에서 사용자 입력 값 추출
        val answers = questionAdapter.getUserAnswers()
        if (answers.size < questionList.size) {
            // 아직 안 쓴 문항이 있음
            Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // seniorId 가져오기
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val seniorId = sharedPrefs.getLong("seniorId", -1L)
        if (seniorId == -1L) {
            Toast.makeText(this, "시니어 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 서버 전송용 Body 생성
        val requestBody = SurveyCreateRequest(
            responses = answers.map { (questionNumber, userAnswer) ->
                SurveyAnswerRequest(
                    questionNumber = questionNumber,
                    answerText = userAnswer.answerText,
                    optionNumber = userAnswer.optionNumbers
                )
            }
        )

        // API 호출
        val service = RetrofitClient.getInstance(this).create(SurveyService::class.java)
        service.submitSurvey(seniorId, requestBody).enqueue(object : Callback<SurveyCreateResponse> {
            override fun onResponse(call: Call<SurveyCreateResponse>, response: Response<SurveyCreateResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == 201) {
                        Toast.makeText(this@RiskAssessmentQuestionActivity,
                            "설문 제출이 완료되었습니다.\n(Survey ID: ${body.data})",
                            Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@RiskAssessmentQuestionActivity,
                            "제출 실패: ${body?.status}",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RiskAssessmentQuestionActivity,
                        "서버 에러: ${response.code()}",
                        Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<SurveyCreateResponse>, t: Throwable) {
                Toast.makeText(this@RiskAssessmentQuestionActivity,
                    "네트워크 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }
}