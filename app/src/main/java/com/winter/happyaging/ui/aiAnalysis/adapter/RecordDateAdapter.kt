package com.winter.happyaging.ui.aiAnalysis.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecordDateAdapter(
    private var dateList: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<RecordDateAdapter.RecordDateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordDateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record_date, parent, false)
        return RecordDateViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordDateViewHolder, position: Int) {
        holder.bind(dateList[position])
    }

    override fun getItemCount(): Int = dateList.size

    fun updateData(newList: List<String>) {
        dateList = newList
        notifyDataSetChanged()
    }

    inner class RecordDateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRecordDate: TextView = itemView.findViewById(R.id.tvRecordDate)
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(date: String) {
            val parsedDate = try {
                LocalDateTime.parse(date)
            } catch (e: Exception) {
                null
            }

            val displayText = if (parsedDate != null) {
                val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 H시 mm분의 분석 기록")
                parsedDate.format(formatter)
            } else {
                date
            }
            tvRecordDate.text = displayText

            itemView.setOnClickListener {
                onItemClick(date)
            }
        }
    }
}