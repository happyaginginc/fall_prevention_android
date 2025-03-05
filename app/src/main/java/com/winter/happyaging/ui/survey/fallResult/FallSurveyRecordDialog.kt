package com.winter.happyaging.ui.survey.fallResult

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.winter.happyaging.R
import com.winter.happyaging.data.survey.model.SurveyResultData

class FallSurveyRecordDialog : DialogFragment() {

    private var survey: SurveyResultData? = null

    companion object {
        private const val ARG_SURVEY = "arg_survey"

        fun newInstance(survey: SurveyResultData): FallSurveyRecordDialog {
            val dialog = FallSurveyRecordDialog()
            val bundle = Bundle().apply { putSerializable(ARG_SURVEY, survey) }
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        survey = arguments?.getSerializable(ARG_SURVEY) as? SurveyResultData
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.dialog_fall_survey_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 다이얼로그 사이즈 및 배경 설정
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 뷰 바인딩
        val tvDialogTitle = view.findViewById<TextView>(R.id.tvDialogTitle)
        val tvDialogContent = view.findViewById<TextView>(R.id.tvDialogContent)
        val tvDialogRiskLevel = view.findViewById<TextView>(R.id.tvDialogRiskLevel)
        val btnConfirm = view.findViewById<TextView>(R.id.btnDialogConfirm)

        tvDialogTitle.text = "설문결과"
        tvDialogRiskLevel.text = "위험등급: ${survey?.riskLevel}"
        tvDialogContent.text = survey?.summary ?: "결과 요약이 없습니다."

        btnConfirm.setOnClickListener {
            val pdfUrl = survey?.pdfUrl
            if (!pdfUrl.isNullOrEmpty()) {
                downloadPdfUsingDownloadManager(pdfUrl, "fall_survey_${survey?.surveyId}.pdf")
                dismiss()
            } else {
                Toast.makeText(requireContext(), "PDF 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadPdfUsingDownloadManager(pdfUrl: String, fileName: String) {
        try {
            val completeUrl = "https://api.happy-aging.co.kr/downloadPDF?filename=$pdfUrl"

            val uri = Uri.parse(completeUrl)
            val request = DownloadManager.Request(uri)
                .setTitle("Fall Survey PDF")
                .setDescription("Downloading survey result PDF")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(requireContext(), "다운로드가 시작되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "다운로드 시작 중 오류 발생", Toast.LENGTH_SHORT).show()
        }
    }

}
