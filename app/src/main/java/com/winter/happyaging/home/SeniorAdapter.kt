package com.winter.happyaging.home

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R
import com.winter.happyaging.ReqDTO.SeniorRequest
import java.util.Calendar

class SeniorAdapter(
    private var seniorList: List<SeniorData>,
    private val onItemClick: (SeniorData) -> Unit
) : RecyclerView.Adapter<SeniorAdapter.SeniorViewHolder>() {

    class SeniorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.tvSeniorName2)
        val addressTextView: TextView = view.findViewById(R.id.tvSeniorAddress2)
        val ageTextView: TextView = view.findViewById(R.id.tvSeniorAge2)
        val manageButton: Button = view.findViewById(R.id.btnManage2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeniorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_senior, parent, false)
        return SeniorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeniorViewHolder, position: Int) {
        val senior = seniorList[position]
        holder.nameTextView.text = senior.name
        holder.addressTextView.text = "${senior.address}"
        holder.ageTextView.text = "${calculateAge(senior.birthYear)}세"

        holder.manageButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ManageSeniorActivity::class.java).apply {
                putExtra("name", senior.name)
                putExtra("address", senior.address)
                putExtra("birthYear", senior.birthYear)
                putExtra("sex", senior.sex)
                putExtra("phoneNumber", senior.phoneNumber)
                putExtra("relationship", senior.relationship)
                putExtra("memo", senior.memo)
            }
            Log.d("SeniorAdapter", "🚀 ManageSeniorActivity 실행: $senior")
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = seniorList.size

    private fun calculateAge(birthYear: Int): Int {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return currentYear - birthYear
    }

    fun updateData(newList: List<SeniorData>) {
        seniorList = newList
        notifyDataSetChanged() // 🚀 데이터 변경 후 UI 갱신
    }
}
