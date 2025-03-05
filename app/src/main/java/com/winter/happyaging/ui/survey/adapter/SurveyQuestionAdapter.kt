package com.winter.happyaging.ui.survey.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.data.survey.model.SurveyQuestionResponse
import com.winter.happyaging.data.survey.model.UserAnswer
import com.winter.happyaging.databinding.ItemSurveyQuestionMultipleBinding
import com.winter.happyaging.databinding.ItemSurveyQuestionShortBinding
import com.winter.happyaging.databinding.ItemSurveyQuestionTrueFalseBinding

class SurveyQuestionAdapter(
    private val questionList: List<SurveyQuestionResponse>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SHORT_ANSWER = 1
        private const val TYPE_TRUE_FALSE = 2
        private const val TYPE_MULTIPLE_CHOICE = 3
    }

    private val userAnswers = mutableMapOf<Int, UserAnswer>()

    override fun getItemViewType(position: Int): Int {
        return when (questionList[position].type) {
            "SHORT_ANSWER" -> TYPE_SHORT_ANSWER
            "TRUE_FALSE" -> TYPE_TRUE_FALSE
            "MULTIPLE_CHOICE" -> TYPE_MULTIPLE_CHOICE
            else -> TYPE_SHORT_ANSWER
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

    inner class ShortAnswerViewHolder(private val binding: ItemSurveyQuestionShortBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(question: SurveyQuestionResponse) {
            binding.tvQuestionNumber.text = "Q${question.questionNumber}"
            binding.tvQuestionContent.text = question.content

            val savedAnswer = userAnswers[question.questionNumber]?.answerText
            binding.edtAnswer.setText(savedAnswer ?: "")

            binding.edtAnswer.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val answerText = s?.toString().orEmpty()
                    val userAnswer = userAnswers.getOrElse(question.questionNumber) { UserAnswer() }
                    userAnswer.answerText = answerText
                    userAnswer.optionNumbers = emptyList()
                    userAnswers[question.questionNumber] = userAnswer
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    inner class TrueFalseViewHolder(private val binding: ItemSurveyQuestionTrueFalseBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(question: SurveyQuestionResponse) {
            binding.tvQuestionNumber.text = "Q${question.questionNumber}"
            binding.tvQuestionContent.text = question.content

            if (question.options.size == 2) {
                binding.rbYes.text = question.options[0].content
                binding.rbNo.text = question.options[1].content
            }

            val saved = userAnswers[question.questionNumber]?.optionNumbers
            if (saved != null && saved.isNotEmpty()) {
                when (saved[0]) {
                    question.options[0].optionNumber -> binding.rbYes.isChecked = true
                    question.options[1].optionNumber -> binding.rbNo.isChecked = true
                }
            } else {
                binding.radioGroup.clearCheck()
            }

            binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val userAnswer = userAnswers.getOrElse(question.questionNumber) { UserAnswer() }
                userAnswer.answerText = null
                userAnswer.optionNumbers = when (checkedId) {
                    binding.rbYes.id -> listOf(question.options[0].optionNumber)
                    binding.rbNo.id -> listOf(question.options[1].optionNumber)
                    else -> emptyList()
                }
                userAnswers[question.questionNumber] = userAnswer
            }
        }
    }

    inner class MultipleChoiceViewHolder(private val binding: ItemSurveyQuestionMultipleBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(question: SurveyQuestionResponse) {
            binding.tvQuestionNumber.text = "Q${question.questionNumber}"
            binding.tvQuestionContent.text = question.content

            binding.rgMultipleChoice.removeAllViews()

            question.options.forEach { option ->
                val radioButton = RadioButton(binding.root.context).apply {
                    text = option.content
                    id = View.generateViewId()
                }
                binding.rgMultipleChoice.addView(radioButton)
            }

            val saved = userAnswers[question.questionNumber]?.optionNumbers
            if (saved != null && saved.isNotEmpty()) {
                val index = question.options.indexOfFirst { it.optionNumber == saved[0] }
                if (index in 0 until binding.rgMultipleChoice.childCount) {
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

    fun getUserAnswers(): Map<Int, UserAnswer> = userAnswers
}
