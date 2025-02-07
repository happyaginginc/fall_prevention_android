package com.winter.happyaging.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R

class QuestionAdapter(private val questions: List<Question>) :
    RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionNumber: TextView = view.findViewById(R.id.questionNumber)
        val questionText: TextView = view.findViewById(R.id.questionText)
        val choicesRecyclerView: RecyclerView = view.findViewById(R.id.choicesRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]

        holder.questionNumber.text = "${position + 1} / ${questions.size}"
        holder.questionText.text = question.text

        // 선택지 RecyclerView 설정
        holder.choicesRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.choicesRecyclerView.adapter = ChoiceAdapter(question.choices)
    }

    override fun getItemCount(): Int = questions.size
}
