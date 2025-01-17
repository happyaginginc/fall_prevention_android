package com.winter.happyaging.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.winter.happyaging.AIFirstFragment
import com.winter.happyaging.R

class AIAnalysisFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ai_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val btnStartAnalysis = view.findViewById<Button>(R.id.btnStartAnalysis)
        btnStartAnalysis.setOnClickListener {
            // AIFirstFragment로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AIFirstFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
