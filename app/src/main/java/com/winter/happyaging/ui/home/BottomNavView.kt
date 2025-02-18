package com.winter.happyaging.ui.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.winter.happyaging.R

class BottomNavView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.bottom_nav, this, true)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        findViewById<LinearLayout>(R.id.nav_senior_list).setOnClickListener {
        }

        // 나머지 메뉴 클릭 시 개발 중 토스트 출력
        val underDevMenus = listOf(
            findViewById<LinearLayout>(R.id.nav_knowledge),
            findViewById<LinearLayout>(R.id.nav_prevention_goods),
            findViewById<LinearLayout>(R.id.nav_prevention_exercise)
        )

        underDevMenus.forEach { menu ->
            menu.setOnClickListener {
                Toast.makeText(context, "개발 중입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}