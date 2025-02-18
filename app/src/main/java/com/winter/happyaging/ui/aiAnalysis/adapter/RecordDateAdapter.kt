package com.winter.happyaging.ui.aiAnalysis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R

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
        fun bind(date: String) {
            tvRecordDate.text = date
            itemView.setOnClickListener {
                onItemClick(date)
            }
        }
    }
}