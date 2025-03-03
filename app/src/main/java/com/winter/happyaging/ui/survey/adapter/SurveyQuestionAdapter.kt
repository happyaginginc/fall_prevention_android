package com.winter.happyaging.ui.survey.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.data.survey.model.SurveyQuestionResponse
import com.winter.happyaging.databinding.ItemSurveyQuestionMultipleBinding
import com.winter.happyaging.databinding.ItemSurveyQuestionShortBinding
import com.winter.happyaging.databinding.ItemSurveyQuestionTrueFalseBinding

/**
 * question.type 에 따라 3가지 ViewHolder 로 구분:
 * 1) SHORT_ANSWER
 * 2) TRUE_FALSE
 * 3) MULTIPLE_CHOICE
 *
 * 사용자가 입력한 답을 저장하기 위해 userAnswers 맵 활용
 */
class SurveyQuestionAdapter(
    private val questionList: List<SurveyQuestionResponse>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SHORT_ANSWER = 1
        private const val TYPE_TRUE_FALSE = 2
        private const val TYPE_MULTIPLE_CHOICE = 3
    }

    // 사용자가 선택하거나 입력한 답변을 임시로 저장
    // key: questionNumber, value: UserAnswer
    private val userAnswers = mutableMapOf<Int, UserAnswer>()

    override fun getItemViewType(position: Int): Int {
        return when (questionList[position].type) {
            "SHORT_ANSWER" -> TYPE_SHORT_ANSWER
            "TRUE_FALSE" -> TYPE_TRUE_FALSE
            "MULTIPLE_CHOICE" -> TYPE_MULTIPLE_CHOICE
            else -> TYPE_SHORT_ANSWER  // 기본
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_SHORT_ANSWER -> {
                val binding = ItemSurveyQuestionShortBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ShortAnswerViewHolder(binding)
            }
            TYPE_TRUE_FALSE -> {
                val binding = ItemSurveyQuestionTrueFalseBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                TrueFalseViewHolder(binding)
            }
            TYPE_MULTIPLE_CHOICE -> {
                val binding = ItemSurveyQuestionMultipleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MultipleChoiceViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun getItemCount(): Int = questionList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val question = questionList[position]
        when (holder) {
            is ShortAnswerViewHolder -> holder.bind(question)
            is TrueFalseViewHolder -> holder.bind(question)
            is MultipleChoiceViewHolder -> holder.bind(question)
        }
    }

    /**
     * 1) SHORT_ANSWER
     */
    inner class ShortAnswerViewHolder(private val binding: ItemSurveyQuestionShortBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(question: SurveyQuestionResponse) {
            // 질문 표시
            binding.tvQuestionNumber.text = "Q${question.questionNumber}"
            binding.tvQuestionContent.text = question.content

            // 이미 입력한 값이 있으면 setText
            val savedAnswer = userAnswers[question.questionNumber]?.answerText
            if (!savedAnswer.isNullOrEmpty()) {
                binding.edtAnswer.setText(savedAnswer)
            } else {
                binding.edtAnswer.setText("")
            }

            binding.edtAnswer.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val answerText = s?.toString().orEmpty()
                    // userAnswers 맵 갱신
                    val userAnswer = userAnswers.getOrElse(question.questionNumber) {
                        UserAnswer()
                    }
                    userAnswer.answerText = answerText
                    userAnswer.optionNumbers = emptyList()
                    userAnswers[question.questionNumber] = userAnswer
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    /**
     * 2) TRUE_FALSE (예/아니오)
     */
    inner class TrueFalseViewHolder(private val binding: ItemSurveyQuestionTrueFalseBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(question: SurveyQuestionResponse) {
            binding.tvQuestionNumber.text = "Q${question.questionNumber}"
            binding.tvQuestionContent.text = question.content

            // 주어진 옵션(예/아니오)을 라디오 버튼에 세팅
            // question.options[0] -> 예, question.options[1] -> 아니오 (가정)
            if (question.options.size == 2) {
                binding.rbYes.text = question.options[0].content  // "예"
                binding.rbNo.text = question.options[1].content   // "아니오"
            }

            // 이미 선택된 값 복원
            val saved = userAnswers[question.questionNumber]?.optionNumbers
            if (saved != null && saved.isNotEmpty()) {
                val selectedOption = saved[0]
                if (selectedOption == question.options[0].optionNumber) {
                    binding.rbYes.isChecked = true
                } else if (selectedOption == question.options[1].optionNumber) {
                    binding.rbNo.isChecked = true
                }
            } else {
                binding.radioGroup.clearCheck()
            }

            binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val userAnswer = userAnswers.getOrElse(question.questionNumber) { UserAnswer() }
                userAnswer.answerText = null
                userAnswer.optionNumbers = when(checkedId) {
                    binding.rbYes.id -> listOf(question.options[0].optionNumber)
                    binding.rbNo.id -> listOf(question.options[1].optionNumber)
                    else -> emptyList()
                }
                userAnswers[question.questionNumber] = userAnswer
            }
        }
    }

    /**
     * 3) MULTIPLE_CHOICE (보기 여러 개) - 여기서는 단일선택(라디오그룹) 예시
     *    실제로 다중선택 필요하면 CheckBox 로 구성 가능
     */
    inner class MultipleChoiceViewHolder(private val binding: ItemSurveyQuestionMultipleBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(question: SurveyQuestionResponse) {
            binding.tvQuestionNumber.text = "Q${question.questionNumber}"
            binding.tvQuestionContent.text = question.content

            // 라디오그룹 동적 생성(단일선택)
            binding.rgMultipleChoice.removeAllViews()

            question.options.forEach { option ->
                val radioButton = RadioButton(binding.root.context)
                radioButton.text = option.content
                radioButton.id = View.generateViewId()
                binding.rgMultipleChoice.addView(radioButton)
            }

            // 저장된 선택 복원
            val saved = userAnswers[question.questionNumber]?.optionNumbers
            if (saved != null && saved.isNotEmpty()) {
                // optionNumber -> 몇 번째인지 찾아서 체크
                val savedOptionNumber = saved[0]
                val index = question.options.indexOfFirst { it.optionNumber == savedOptionNumber }
                if (index >= 0 && index < binding.rgMultipleChoice.childCount) {
                    (binding.rgMultipleChoice.getChildAt(index) as? RadioButton)?.isChecked = true
                }
            }

            binding.rgMultipleChoice.setOnCheckedChangeListener { group, checkedId ->
                val radioButtonIndex = group.indexOfChild(group.findViewById(checkedId))
                if (radioButtonIndex >= 0) {
                    val selectedOptionNumber = question.options[radioButtonIndex].optionNumber
                    val userAnswer = userAnswers.getOrElse(question.questionNumber) { UserAnswer() }
                    userAnswer.answerText = null
                    userAnswer.optionNumbers = listOf(selectedOptionNumber)
                    userAnswers[question.questionNumber] = userAnswer
                }
            }
        }
    }

    /**
     * 사용자가 입력한 모든 답변 가져오기
     */
    fun getUserAnswers(): Map<Int, UserAnswer> {
        // 아직 답변하지 않은 질문이 있다면 size가 questionList.size 보다 작음
        // 여기서는 userAnswers 자체를 그대로 반환
        return userAnswers
    }

    /**
     * 답변 저장용 내부 데이터 클래스
     */
    data class UserAnswer(
        var answerText: String? = null,
        var optionNumbers: List<Int> = emptyList()
    )
}