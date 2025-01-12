package com.winter.happyaging

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.winter.happyaging.login.FirstFragment
import com.winter.happyaging.login.LoginFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#B8660E")
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 앱 실행 시 가장 먼저 보여줄 인트로/웰컴 프래그먼트
        if (savedInstanceState == null) {
            showFragment(FirstFragment())
        }

        // 레이아웃에서 "이미 계정이 있으신가요?" TextView 가져오기
        val loginLink: TextView = findViewById(R.id.loginLink)

        // 클릭 이벤트 설정
        loginLink.setOnClickListener {
            // LoginFragment로 교체
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, LoginFragment())
                addToBackStack(null) // 뒤로가기 시 이전 화면 복귀 가능
            }
        }
    }

    fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)  // 뒤로가기 누르면 이전 프래그먼트로 돌아가기 가능
            .commit()
    }
}
