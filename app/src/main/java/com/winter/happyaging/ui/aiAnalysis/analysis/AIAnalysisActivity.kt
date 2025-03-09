package com.winter.happyaging.ui.aiAnalysis.analysis

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.winter.happyaging.R
import com.winter.happyaging.databinding.ActivityAiAnalysisBinding

class AIAnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAiAnalysisBinding
    private val TAG = "AIAnalysisActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_analysis) as NavHostFragment
        navHostFragment.navController.setGraph(R.navigation.nav_graph)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        try {
            currentFocus?.let { view ->
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in dispatchTouchEvent", e)
        }
        return super.dispatchTouchEvent(ev)
    }
}