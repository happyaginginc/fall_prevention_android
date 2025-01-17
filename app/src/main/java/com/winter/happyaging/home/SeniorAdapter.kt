package com.winter.happyaging.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R

class SeniorAdapter(
    private val items: List<SeniorData>,
    private val onManageClick: (SeniorData) -> Unit // 관리하기 버튼 클릭 시 콜백
) : RecyclerView.Adapter<SeniorAdapter.SeniorViewHolder>() {

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeniorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_senior, parent, false)
        return SeniorViewHolder(view)
    }

    // ViewHolder와 데이터를 바인딩
    override fun onBindViewHolder(holder: SeniorViewHolder, position: Int) {
        val seniorData = items[position]
        holder.bind(seniorData, onManageClick)
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int = items.size

    // ViewHolder 클래스 정의
    class SeniorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvSeniorName)
        private val tvInfo: TextView = itemView.findViewById(R.id.tvSeniorInfo)
        private val btnManage: Button = itemView.findViewById(R.id.btnManage)

        // 데이터와 클릭 이벤트를 바인딩
        fun bind(data: SeniorData, onManageClick: (SeniorData) -> Unit) {
            tvName.text = data.name
            tvInfo.text = data.info

            // "관리하기" 버튼 클릭 시 콜백 호출
            btnManage.setOnClickListener {
                onManageClick(data)
            }
        }
    }
}
