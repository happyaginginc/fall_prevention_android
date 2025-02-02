package com.winter.happyaging.home

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.winter.happyaging.R

class ManageSeniorActivity : AppCompatActivity() {

    private var name: String? = null
    private var info: String? = null
    private var grade: Int? = null

    companion object {
        const val EXTRA_NAME = "name"
        const val EXTRA_INFO = "info"
        const val EXTRA_GRADE = "grade"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_senior)

        // Intent로 전달된 데이터 가져오기
        name = intent.getStringExtra(EXTRA_NAME)
        info = intent.getStringExtra(EXTRA_INFO)
        grade = intent.getIntExtra(EXTRA_GRADE, -1)

        Log.d("ManageSeniorActivity", "관리하기 버튼 클릭됨")

        // 데이터를 UI에 반영
        findViewById<TextView>(R.id.tvSeniorName).text = name ?: "이름 없음"
        findViewById<TextView>(R.id.tvSeniorInfo).text = info ?: "정보 없음"
    }
}
