package com.winter.happyaging.ui.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R
import com.winter.happyaging.data.question.model.Question
import com.winter.happyaging.data.survey.model.Survey
import com.winter.happyaging.ui.home.adapter.QuestionAdapter

class SurveyFragment : Fragment() {

    private lateinit var questionsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_survey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기화
        questionsRecyclerView = view.findViewById(R.id.questionsRecyclerView)
        questionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 백엔드에서 데이터를 받아옴 (더미 데이터 예시)
        val surveyData = fetchSurveyData()

        // RecyclerView에 데이터 설정
        questionsRecyclerView.adapter = QuestionAdapter(surveyData.questions)
    }

    // 더미 데이터 생성 (API 연동 시 이 부분을 네트워크 호출로 대체)
    private fun fetchSurveyData(): Survey {
        return Survey(
            totalQuestions = 60,
            questions = listOf(
                Question(
                    id = 1,
                    text = "Q1. 최근 몇 달 동안 낙상 경험이 있으신가요?",
                    choices = listOf("1-2회", "3-4회", "5회 이상")
                ),
                Question(
                    id = 2,
                    text = "Q2. 보조 도구를 사용하시나요?",
                    choices = listOf("예", "아니오")
                )
            )
        )
    }
}
