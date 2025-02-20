package com.winter.happyaging.ui.home.senior.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.data.senior.model.SeniorReadResponse
import com.winter.happyaging.databinding.ItemSeniorBinding
import com.winter.happyaging.ui.home.senior.ManageSeniorActivity
import java.util.Calendar

class SeniorAdapter(private var seniorList: List<SeniorReadResponse>) : RecyclerView.Adapter<SeniorAdapter.SeniorViewHolder>() {

    inner class SeniorViewHolder(private val binding: ItemSeniorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(senior: SeniorReadResponse) {
            binding.tvSeniorName.text = senior.name
            binding.tvSeniorAddress.text = senior.address
            binding.tvSeniorAge.text = "${calculateAge(senior.birthYear)}세"
            binding.btnManage.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ManageSeniorActivity::class.java).apply {
                    putExtra("name", senior.name)
                    putExtra("address", senior.address)
                    putExtra("birthYear", senior.birthYear)
                    putExtra("sex", senior.sex)
                    putExtra("phoneNumber", senior.phoneNumber)
                    putExtra("relationship", senior.relationship)
                    putExtra("memo", senior.memo)
                    putExtra("seniorId", senior.seniorId)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeniorViewHolder {
        val binding = ItemSeniorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeniorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeniorViewHolder, position: Int) {
        holder.bind(seniorList[position])
    }

    override fun getItemCount(): Int = seniorList.size

    private fun calculateAge(birthYear: Int): Int {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return currentYear - birthYear
    }

    fun updateData(newList: List<SeniorReadResponse>) {
        seniorList = newList
        notifyDataSetChanged() // 데이터 변경 후 UI 갱신
    }
}