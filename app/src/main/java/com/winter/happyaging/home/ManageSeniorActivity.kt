package com.winter.happyaging.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.winter.happyaging.R
import com.winter.happyaging.ai.AIAnalysisActivity

class ManageSeniorActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvAge: TextView
    private lateinit var balanceBtn: LinearLayout
    private lateinit var cameraBtn: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_senior)  // ✅ XML 파일 로드

        tvName = findViewById(R.id.tvSeniorName2)
        tvAddress = findViewById(R.id.tvSeniorAddress2)
        tvAge = findViewById(R.id.tvSeniorAge2)
        balanceBtn = findViewById(R.id.balanceBtn)
        cameraBtn = findViewById(R.id.cameraBtn)

        val name = intent.getStringExtra("name") ?: "이름 없음"
        val address = intent.getStringExtra("address") ?: "주소 없음"
        val birthYear = intent.getIntExtra("birthYear", -1)

        // 데이터 표시
        tvName.text = name
        tvAddress.text = address
        tvAge.text = if (birthYear > 1900) "나이: ${calculateAge(birthYear)}세" else "나이 정보 없음"

        Log.d("ManageSeniorActivity", "관리하기 버튼 클릭됨 - $name")

        balanceBtn.setOnClickListener {
            val intent = Intent(this, SurveyReadyActivity::class.java)
            startActivity(intent)
        }

        cameraBtn.setOnClickListener {
            val intent = Intent(this, AIAnalysisActivity::class.java)
            startActivity(intent)
        }
    }

    private fun calculateAge(birthYear: Int?): Int {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        return if (birthYear != null && birthYear > 1900) currentYear - birthYear else -1
    }
}
