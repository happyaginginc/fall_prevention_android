package com.winter.happyaging.ui.survey.fallResult

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.winter.happyaging.R

class FallSurveyRecordListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fall_survey_record_list)

        if (savedInstanceState == null) {
            val fragment = FallSurveyRecordListFragment().apply {
                arguments = intent.extras
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_fall, fragment)
                .commit()
        }
    }
}
