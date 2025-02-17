package com.winter.happyaging.ui.aiAnalysis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.winter.happyaging.R
import com.winter.happyaging.databinding.ActivityAiAnalysisBinding

class AIAnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAiAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_analysis) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph)
    }
}
