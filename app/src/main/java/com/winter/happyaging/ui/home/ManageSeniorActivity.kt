// ManageSeniorActivity.kt
package com.winter.happyaging.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.winter.happyaging.R
import com.winter.happyaging.ui.aiAnalysis.AIAnalysisActivity
import com.winter.happyaging.ui.aiAnalysis.AnalysisRecordListActivity

class ManageSeniorActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvAge: TextView
    private lateinit var balanceBtn: LinearLayout
    private lateinit var fallResultBtn: LinearLayout
    private lateinit var cameraBtn: LinearLayout
    private lateinit var resultBtn: LinearLayout
    private lateinit var backBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_senior)  // XML 파일 로드

        tvName = findViewById(R.id.tvSeniorName2)
        tvAddress = findViewById(R.id.tvSeniorAddress2)
        tvAge = findViewById(R.id.tvSeniorAge2)
        balanceBtn = findViewById(R.id.balanceBtn)
        fallResultBtn = findViewById(R.id.fallResultBtn)
        cameraBtn = findViewById(R.id.cameraBtn)
        resultBtn = findViewById(R.id.resultBtn)
        backBtn = findViewById(R.id.btnBack)

        val seniorId = intent.getLongExtra("seniorId", -1L)
        val name = intent.getStringExtra("name") ?: "이름 없음"
        val address = intent.getStringExtra("address") ?: "주소 없음"
        val birthYear = intent.getIntExtra("birthYear", -1)

        // 데이터 표시
        tvName.text = name
        tvAddress.text = address
        tvAge.text = if (birthYear > 1900) "나이: ${calculateAge(birthYear)}세" else "나이 정보 없음"

        // seniorId를 SharedPreferences에 저장
        getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .edit()
            .putLong("seniorId", seniorId)
            .apply()

        // 낙상 위험등급 측정 버튼 (준비 중)
        balanceBtn.setOnClickListener {
            Toast.makeText(this, "준비 중입니다", Toast.LENGTH_SHORT).show()
        }

        // 낙상 위험등급 결과 확인 버튼 (준비 중)
        fallResultBtn.setOnClickListener {
            Toast.makeText(this, "준비 중입니다", Toast.LENGTH_SHORT).show()
        }

        // 집 사진 AI 분석 버튼: AIAnalysisActivity 실행
        cameraBtn.setOnClickListener {
            val intent = Intent(this, AIAnalysisActivity::class.java)
            startActivity(intent)
        }

        // 집 사진 AI 분석 결과 확인 버튼: 새로운 액티비티 실행하여 AnalysisRecordListFragment 표시
        resultBtn.setOnClickListener {
            val intent = Intent(this, AnalysisRecordListActivity::class.java);
            startActivity(intent)
        }

        // 뒤로가기 버튼: 액티비티 종료
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun calculateAge(birthYear: Int?): Int {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        return if (birthYear != null && birthYear > 1900) currentYear - birthYear else -1
    }
}