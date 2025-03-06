package com.winter.happyaging.ui.aiAnalysis.record

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.winter.happyaging.R

class AnalysisRecordListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_record_list)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AnalysisRecordListFragment())
                .commit()
        }
    }
}