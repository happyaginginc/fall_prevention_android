package com.winter.happyaging.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.winter.happyaging.R
import com.winter.happyaging.survey.SurveyFragment

class SurveyReadyActivity : AppCompatActivity() {

    private lateinit var btnStartSurvey: Button
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_ready)

        btnStartSurvey = findViewById(R.id.btnStartSurvey)
        btnBack = findViewById(R.id.btnBack)

        btnStartSurvey.setOnClickListener {
            val intent = Intent(this, SurveyFragment::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
