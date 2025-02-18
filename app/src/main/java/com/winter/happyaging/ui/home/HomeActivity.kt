package com.winter.happyaging.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.winter.happyaging.R
import com.winter.happyaging.databinding.ActivityHomeBinding
import com.winter.happyaging.ui.home.senior.SeniorListFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#B8660E")
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 최초에 시니어 목록 프래그먼트를 로드
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SeniorListFragment())
                .commit()
        }

        // 하단바 커스텀 뷰를 컨테이너에 추가
        val bottomNavView = BottomNavView(this)
        binding.root.findViewById<android.widget.FrameLayout>(R.id.bottom_nav_container).apply {
            removeAllViews()
            addView(bottomNavView)
        }
    }
}