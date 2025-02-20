package com.winter.happyaging.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && ev.action == MotionEvent.ACTION_DOWN) {
            val outRect = Rect()
            view.getGlobalVisibleRect(outRect)
            if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                view.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}