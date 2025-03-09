package com.winter.happyaging.ui.survey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.winter.happyaging.data.survey.model.SurveyAnswerRequest
import com.winter.happyaging.data.survey.model.SurveyCreateRequest
import com.winter.happyaging.data.survey.model.SurveyCreateResponse
import com.winter.happyaging.data.survey.model.SurveyQuestionResponse
import com.winter.happyaging.data.survey.model.UserAnswer
import com.winter.happyaging.data.survey.repository.QuestionRepository
import com.winter.happyaging.data.survey.service.SurveyService
import com.winter.happyaging.databinding.ActivityRiskAssessmentQuestionBinding
import com.winter.happyaging.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiskAssessmentQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiskAssessmentQuestionBinding
    private var questionList: List<SurveyQuestionResponse> = emptyList()
    private var currentIndex = 0
    private val userAnswers = mutableMapOf<Int, UserAnswer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiskAssessmentQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.tvHeader.text = "낙상 위험등급 설문"
        binding.header.btnBack.setOnClickListener { handleBackPress() }

        questionList = QuestionRepository.getCachedQuestionList()
        if (questionList.isEmpty()) {
            Toast.makeText(this, "질문 목록이 없습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentIndex = 0
        showQuestion(currentIndex)

        binding.btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                saveCurrentQuestionWithoutValidation(currentIndex)
                currentIndex--
                showQuestion(currentIndex)
            }
        }

        binding.btnNext.setOnClickListener {
            if (!validateAndSaveUserAnswer(currentIndex)) return@setOnClickListener
            if (isLastQuestion(currentIndex)) {
                submitSurveyAnswers()
            } else {
                val nextIndex = findNextQuestionIndex(currentIndex)
                if (nextIndex == -1) {
                    submitSurveyAnswers()
                } else {
                    currentIndex = nextIndex
                    showQuestion(currentIndex)
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })
    }

    private fun handleBackPress() {
        if (!hasUnsavedData()) {
            finish()
        } else {
            showDiscardDialog()
        }
    }

    private fun hasUnsavedData(): Boolean = userAnswers.isNotEmpty()

    private fun showDiscardDialog() {
        AlertDialog.Builder(this)
            .setTitle("뒤로가기")
            .setMessage("현재까지 작성하신 설문 답변이 사라집니다. 정말 뒤로 가시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                userAnswers.clear()
                finish()
            }
            .setNegativeButton("아니오", null)
            .show()
    }

    private fun showQuestion(questionIndex: Int) {
        val question = questionList[questionIndex]
        binding.tvCategory.text = getCategoryKorean(question.category)
        binding.tvQuestionTitle.text = "Q${question.questionNumber}"
        binding.tvQuestionContent.text = question.content

        if (!question.imageUrl.isNullOrEmpty()) {
            binding.ivQuestionImage.visibility = View.VISIBLE
            Glide.with(this)
                .load(question.imageUrl)
                .error(android.R.drawable.stat_notify_error)
                .into(binding.ivQuestionImage)
        } else {
            binding.ivQuestionImage.visibility = View.GONE
        }

        val savedAnswer = userAnswers[question.questionNumber]
        binding.layoutAnswerContainer.removeAllViews()
        val answerView = createAnswerView(question, savedAnswer)
        binding.layoutAnswerContainer.addView(answerView)

        binding.btnPrev.visibility = if (currentIndex > 0) View.VISIBLE else View.INVISIBLE
        binding.btnNext.text = if (isLastQuestion(questionIndex)) "제출" else "다음"
    }

    private fun saveCurrentQuestionWithoutValidation(questionIndex: Int) {
        val question = questionList[questionIndex]
        val answer = collectUserAnswer(question, binding.layoutAnswerContainer)
        userAnswers[question.questionNumber] = answer ?: UserAnswer()
    }

    private fun validateAndSaveUserAnswer(questionIndex: Int): Boolean {
        val question = questionList[questionIndex]
        val answer = collectUserAnswer(question, binding.layoutAnswerContainer) ?: UserAnswer()
        if (!isAnswerValid(question, answer)) return false
        userAnswers[question.questionNumber] = answer
        return true
    }

    private fun createAnswerView(question: SurveyQuestionResponse, savedAnswer: UserAnswer?): View {
        return when (question.type) {
            "SHORT_ANSWER" -> EditText(this).apply {
                hint = getShortAnswerHint(question.questionNumber)
                setText(savedAnswer?.answerText.orEmpty())
            }
            "TRUE_FALSE" -> RadioGroup(this).apply {
                orientation = RadioGroup.VERTICAL
                question.options.forEach { option ->
                    val radioButton = RadioButton(this@RiskAssessmentQuestionActivity).apply {
                        text = option.content
                        id = View.generateViewId()
                        isChecked = savedAnswer?.optionNumbers?.contains(option.optionNumber) == true
                    }
                    addView(radioButton)
                }
            }
            "MULTIPLE_CHOICE" -> LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                question.options.forEach { option ->
                    val checkBox = CheckBox(this@RiskAssessmentQuestionActivity).apply {
                        text = option.content
                        isChecked = savedAnswer?.optionNumbers?.contains(option.optionNumber) == true
                    }
                    addView(checkBox)
                }
            }
            else -> EditText(this).apply {
                hint = "답변을 입력해주세요."
                setText(savedAnswer?.answerText.orEmpty())
            }
        }
    }

    private fun collectUserAnswer(question: SurveyQuestionResponse, container: ViewGroup): UserAnswer? {
        return when (question.type) {
            "SHORT_ANSWER" -> {
                val editText = container.getChildAt(0) as? EditText ?: return null
                UserAnswer(answerText = editText.text.toString().trim(), optionNumbers = emptyList())
            }
            "TRUE_FALSE" -> {
                val radioGroup = container.getChildAt(0) as? RadioGroup ?: return null
                val checkedId = radioGroup.checkedRadioButtonId
                if (checkedId == -1) {
                    UserAnswer(answerText = null, optionNumbers = emptyList())
                } else {
                    val selectedIndex = radioGroup.indexOfChild(radioGroup.findViewById(checkedId))
                    if (selectedIndex >= 0) {
                        val selectedOptionNumber = question.options[selectedIndex].optionNumber
                        UserAnswer(answerText = null, optionNumbers = listOf(selectedOptionNumber))
                    } else {
                        UserAnswer(answerText = null, optionNumbers = emptyList())
                    }
                }
            }
            "MULTIPLE_CHOICE" -> {
                val linearLayout = container.getChildAt(0) as? LinearLayout ?: return null
                val checkedOptionNumbers = mutableListOf<Int>()
                for (i in 0 until linearLayout.childCount) {
                    val checkBox = linearLayout.getChildAt(i) as? CheckBox
                    if (checkBox?.isChecked == true) {
                        checkedOptionNumbers.add(question.options[i].optionNumber)
                    }
                }
                UserAnswer(answerText = null, optionNumbers = checkedOptionNumbers)
            }
            else -> {
                val editText = container.getChildAt(0) as? EditText ?: return null
                UserAnswer(answerText = editText.text.toString().trim(), optionNumbers = emptyList())
            }
        }
    }

    private fun isAnswerValid(question: SurveyQuestionResponse, userAnswer: UserAnswer): Boolean {
        when (question.type) {
            "SHORT_ANSWER" -> {
                val inputText = userAnswer.answerText.orEmpty()
                if (inputText.isBlank()) {
                    Toast.makeText(this, "답변을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }
                when (question.questionNumber) {
                    3 -> {
                        val height = inputText.toIntOrNull()
                        if (height == null || height !in 50..300) {
                            Toast.makeText(this, "키는 50~300 cm 범위의 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show()
                            return false
                        }
                    }
                    4, 5 -> {
                        val weight = inputText.toIntOrNull()
                        if (weight == null || weight !in 1..300) {
                            Toast.makeText(this, "몸무게는 1~300 kg 범위의 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show()
                            return false
                        }
                    }
                }
            }
            "TRUE_FALSE" -> {
                if (userAnswer.optionNumbers.isEmpty()) {
                    Toast.makeText(this, "항목을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
            "MULTIPLE_CHOICE" -> {
                if (userAnswer.optionNumbers.isEmpty()) {
                    Toast.makeText(this, "하나 이상 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
            else -> {
                if (userAnswer.answerText.orEmpty().isBlank()) {
                    Toast.makeText(this, "답변을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }
        return true
    }

    private fun findNextQuestionIndex(currentIndex: Int): Int {
        val currentQuestion = questionList[currentIndex]
        val userAnswer = userAnswers[currentQuestion.questionNumber] ?: return -1
        userAnswer.optionNumbers.forEach { optNumber ->
            val targetOption = currentQuestion.options.firstOrNull { it.optionNumber == optNumber }
            targetOption?.nextQuestionNumber?.let { jumpQNum ->
                val jumpIndex = questionList.indexOfFirst { it.questionNumber == jumpQNum }
                if (jumpIndex != -1) return jumpIndex
            }
        }
        val nextIndex = currentIndex + 1
        return if (nextIndex < questionList.size) nextIndex else -1
    }

    private fun isLastQuestion(index: Int): Boolean = index == questionList.size - 1

    private fun submitSurveyAnswers() {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.btnPrev.visibility = View.GONE
        binding.btnNext.visibility = View.GONE

        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val seniorId = sharedPrefs.getLong("seniorId", -1L)
        if (seniorId == -1L) {
            Toast.makeText(this, "시니어 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            binding.loadingOverlay.visibility = View.GONE
            binding.btnPrev.visibility = if (currentIndex > 0) View.VISIBLE else View.INVISIBLE
            binding.btnNext.visibility = View.VISIBLE
            return
        }

        val responses = userAnswers.map { (questionNumber, userAnswer) ->
            SurveyAnswerRequest(
                questionNumber = questionNumber,
                answerText = userAnswer.answerText?.takeIf { it.isNotBlank() },
                optionNumber = userAnswer.optionNumbers
            )
        }

        val requestBody = SurveyCreateRequest(responses = responses)
        val service = RetrofitClient.getInstance(this).create(SurveyService::class.java)
        service.submitSurvey(seniorId, requestBody).enqueue(object : Callback<SurveyCreateResponse> {
            override fun onResponse(call: Call<SurveyCreateResponse>, response: Response<SurveyCreateResponse>) {
                binding.loadingOverlay.visibility = View.GONE
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == 201) {
                        val newSurveyId = body.data
                        Toast.makeText(
                            this@RiskAssessmentQuestionActivity,
                            "설문 제출이 완료되었습니다.\n(Survey ID: $newSurveyId)",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(
                            this@RiskAssessmentQuestionActivity,
                            RiskAssessmentResultActivity::class.java
                        )
                        intent.putExtra("seniorId", seniorId)
                        intent.putExtra("surveyId", newSurveyId)
                        startActivity(intent)
                        finish()
                    } else {
                        showRetryDialog("제출 실패: ${body?.status}. 다시 시도하시겠습니까?") { submitSurveyAnswers() }
                        binding.btnPrev.visibility = if (currentIndex > 0) View.VISIBLE else View.INVISIBLE
                        binding.btnNext.visibility = View.VISIBLE
                    }
                } else {
                    showRetryDialog("서버 에러: ${response.code()}. 다시 시도하시겠습니까?") { submitSurveyAnswers() }
                    binding.btnPrev.visibility = if (currentIndex > 0) View.VISIBLE else View.INVISIBLE
                    binding.btnNext.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<SurveyCreateResponse>, t: Throwable) {
                binding.loadingOverlay.visibility = View.GONE
                showRetryDialog("네트워크 오류가 발생했습니다. 다시 시도하시겠습니까?") { submitSurveyAnswers() }
                binding.btnPrev.visibility = if (currentIndex > 0) View.VISIBLE else View.INVISIBLE
                binding.btnNext.visibility = View.VISIBLE
            }
        })
    }

    private fun showRetryDialog(message: String, retryAction: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("오류")
            .setMessage(message)
            .setPositiveButton("재시도") { dialog, _ ->
                retryAction()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getCategoryKorean(category: String): String {
        return when (category) {
            "SENIOR_INFO" -> "어르신 정보"
            "DISEASE" -> "질병 관련"
            "BODY" -> "몸 상태"
            "EXERCISE" -> "운동 관련"
            "FALL_EXPERIENCE" -> "낙상 경험"
            "RESIDENT" -> "주거 환경"
            else -> "기타"
        }
    }

    private fun getShortAnswerHint(questionNumber: Int): String {
        return when (questionNumber) {
            3 -> "답변을 입력해주세요. (cm)"
            4, 5 -> "답변을 입력해주세요. (kg)"
            else -> "답변을 입력해주세요."
        }
    }
}