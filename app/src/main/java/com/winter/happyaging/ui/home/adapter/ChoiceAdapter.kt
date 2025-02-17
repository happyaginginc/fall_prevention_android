package com.winter.happyaging.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R

class ChoiceAdapter(private val choices: List<String>) :
    RecyclerView.Adapter<ChoiceAdapter.ChoiceViewHolder>() {

    class ChoiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val choiceText: TextView = view.findViewById(R.id.choiceText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_choice, parent, false)
        return ChoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChoiceViewHolder, position: Int) {
        holder.choiceText.text = choices[position]
        holder.itemView.setOnClickListener {
            // 선택지 클릭 처리
        }
    }

    override fun getItemCount(): Int = choices.size
}
