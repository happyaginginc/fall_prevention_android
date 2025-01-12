package com.winter.happyaging.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R

class SeniorAdapter(
    private val items: List<SeniorData>,
    private val onManageClick: (SeniorData) -> Unit
) : RecyclerView.Adapter<SeniorAdapter.SeniorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeniorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_senior, parent, false)
        return SeniorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeniorViewHolder, position: Int) {
        holder.bind(items[position], onManageClick)
    }

    override fun getItemCount(): Int = items.size

    class SeniorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvSeniorName)
        private val tvInfo = itemView.findViewById<TextView>(R.id.tvSeniorInfo)
        private val btnManage = itemView.findViewById<Button>(R.id.btnManage)

        fun bind(data: SeniorData, onManageClick: (SeniorData) -> Unit) {
            tvName.text = data.name
            tvInfo.text = data.info
            // 등급 정보나 뷰를 추가로 세팅할 수도 있음

            btnManage.setOnClickListener {
                onManageClick(data)
            }
        }
    }
}
