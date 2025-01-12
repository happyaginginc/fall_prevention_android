package com.winter.happyaging.home

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R

class HomeActivity : AppCompatActivity() {

    // 기존에 뷰를 참조하던 변수들
    private lateinit var ivLogo: ImageView
    private lateinit var edtSearchSenior: EditText
    private lateinit var ivSearch: ImageView
    private lateinit var btnRegisterSenior: Button
//    private lateinit var tvSeniorName: TextView
//    private lateinit var tvSeniorInfo: TextView
//    private lateinit var btnGrade: Button
//    private lateinit var btnManage: Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var seniorAdapter: SeniorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1) 뷰 연결
        ivLogo = findViewById(R.id.ivLogo)
        edtSearchSenior = findViewById(R.id.edtSearchSenior)
        ivSearch = findViewById(R.id.ivSearch)
        btnRegisterSenior = findViewById(R.id.btnRegisterSenior)
//        tvSeniorName = findViewById(R.id.tvSeniorName)
//        tvSeniorInfo = findViewById(R.id.tvSeniorInfo)
//        btnGrade = findViewById(R.id.btnGrade)
//        btnManage = findViewById(R.id.btnManage)

        recyclerView = findViewById(R.id.recyclerSenior)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dummy = listOf(
            SeniorData("박재영", "광주 / 27세)", 1),
            SeniorData("이상민", "대구 / 22세", 2)
        )

        seniorAdapter = SeniorAdapter(dummy) { selectedSenior ->
            val fragment = ManageSeniorFragment.newInstance(
                name = selectedSenior.name,
                info = selectedSenior.info,
                grade = selectedSenior.grade
            )
            showFragment(fragment)
        }
        recyclerView.adapter = seniorAdapter

        // 2) 버튼 리스너

        // (A) "시니어 등록하기" 버튼 → RegisterSeniorFragment로 전환
        btnRegisterSenior.setOnClickListener {
            showFragment(RegisterSeniorFragment())
        }

        // (C) 검색 아이콘 클릭
        ivSearch.setOnClickListener {
            val keyword = edtSearchSenior.text.toString()
            Toast.makeText(this, "검색: $keyword", Toast.LENGTH_SHORT).show()
            // TODO: 실제 검색 로직 (서버/DB)
        }

    }

    /**
     * 프래그먼트를 교체하는 함수
     * activity_home.xml 안에 있는 FrameLayout (ex: R.id.fragmentContainer)에
     * 전달받은 프래그먼트를 replace & addToBackStack
     */
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer2, fragment)
            .addToBackStack(null)
            .commit()
    }
}
