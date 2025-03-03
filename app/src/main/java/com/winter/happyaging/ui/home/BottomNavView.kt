package com.winter.happyaging.ui.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.winter.happyaging.R
import com.winter.happyaging.ui.home.knowledge.KnowledgeFragment
import com.winter.happyaging.ui.home.more.MoreFragment
import com.winter.happyaging.ui.home.product.PreventionGoodsFragment
import com.winter.happyaging.ui.home.senior.SeniorListFragment

class BottomNavView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.bottom_nav, this, true)

        resetSelection()
        findViewById<LinearLayout>(R.id.nav_senior_list).isSelected = true
        setupClickListeners()
    }

    private fun resetSelection() {
        findViewById<LinearLayout>(R.id.nav_senior_list).isSelected = false
        findViewById<LinearLayout>(R.id.nav_knowledge).isSelected = false
        findViewById<LinearLayout>(R.id.nav_prevention_goods).isSelected = false
        findViewById<LinearLayout>(R.id.nav_more).isSelected = false
    }

    private fun setupClickListeners() {
        // 시니어 목록
        findViewById<LinearLayout>(R.id.nav_senior_list).setOnClickListener { view ->
            resetSelection()
            view.isSelected = true
            if (context is AppCompatActivity) {
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, SeniorListFragment())
                    .commit()
            }
        }
        // 관련 지식
        findViewById<LinearLayout>(R.id.nav_knowledge).setOnClickListener { view ->
            resetSelection()
            view.isSelected = true
            if (context is AppCompatActivity) {
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, KnowledgeFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        // 예방 물품
        findViewById<LinearLayout>(R.id.nav_prevention_goods).setOnClickListener { view ->
            resetSelection()
            view.isSelected = true
            if (context is AppCompatActivity) {
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, PreventionGoodsFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        // 더보기
        findViewById<LinearLayout>(R.id.nav_more).setOnClickListener { view ->
            resetSelection()
            view.isSelected = true
            if (context is AppCompatActivity) {
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, MoreFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}