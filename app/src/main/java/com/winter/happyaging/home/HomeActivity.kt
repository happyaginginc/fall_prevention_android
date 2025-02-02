package com.winter.happyaging.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R

class HomeActivity : AppCompatActivity() {

    private lateinit var ivLogo: ImageView
    private lateinit var edtSearchSenior: EditText
    private lateinit var ivSearch: ImageView
    private lateinit var btnRegisterSenior: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var seniorAdapter: SeniorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#B8660E")
        setContentView(R.layout.activity_home)

        // 1) 뷰 연결
        ivLogo = findViewById(R.id.ivLogo)
        edtSearchSenior = findViewById(R.id.edtSearchSenior)
        ivSearch = findViewById(R.id.ivSearch)
        btnRegisterSenior = findViewById(R.id.btnRegisterSenior)
        recyclerView = findViewById(R.id.recyclerSenior)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dummy = listOf(
            SeniorData("박재영", "광주 / 27세", 1),
            SeniorData("이상민", "대구 / 22세", 2)
        )

        // 2) RecyclerView Adapter 설정
        seniorAdapter = SeniorAdapter(dummy) { selectedSenior ->
            // ManageSeniorActivity로 이동
            val intent = Intent(this, ManageSeniorActivity::class.java).apply {
                putExtra(ManageSeniorActivity.EXTRA_NAME, selectedSenior.name)
                putExtra(ManageSeniorActivity.EXTRA_INFO, selectedSenior.info)
                putExtra(ManageSeniorActivity.EXTRA_GRADE, selectedSenior.grade)
            }
            startActivity(intent)
        }
        recyclerView.adapter = seniorAdapter

        // 3) 버튼 리스너

        // "시니어 등록하기" 버튼 → RegisterSeniorFragment로 전환
        btnRegisterSenior.setOnClickListener {
            val intent = Intent(this, RegisterSeniorFragment::class.java)
            startActivity(intent)
        }

        // 검색 아이콘 클릭
        ivSearch.setOnClickListener {
            val keyword = edtSearchSenior.text.toString()
            Toast.makeText(this, "검색: $keyword", Toast.LENGTH_SHORT).show()
            // TODO: 실제 검색 로직 (서버/DB)
        }
    }
}
