package com.winter.happyaging.ui.survey

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.winter.happyaging.data.survey.model.SurveyResultData
import com.winter.happyaging.data.survey.service.SurveyService
import com.winter.happyaging.databinding.ActivityRiskAssessmentResultBinding
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.model.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiskAssessmentResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiskAssessmentResultBinding
    private var pdfUrl: String? = null
    private var riskLevel: Int? = null
    private var summary: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiskAssessmentResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 공통 헤더
        binding.header.tvHeader.text = "낙상 위험등급 결과"
        binding.header.btnBack.setOnClickListener { finish() }

        // 넘겨받은 seniorId / surveyId
        val seniorId = intent.getLongExtra("seniorId", -1L)
        val surveyId = intent.getIntExtra("surveyId", -1)

        if (seniorId == -1L || surveyId == -1) {
            Toast.makeText(this, "결과 조회에 필요한 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 서버에서 결과 가져오기
        fetchSurveyResult(seniorId, surveyId)

        // PDF 다운로드 버튼
        binding.btnDownloadPdf.setOnClickListener {
            pdfUrl?.let { url ->
                downloadPdfUsingDownloadManager(url, "riskReport.pdf")
            } ?: Toast.makeText(this, "PDF 경로가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchSurveyResult(seniorId: Long, surveyId: Int) {
        binding.loadingLayout.visibility = View.VISIBLE

        val service = RetrofitClient.getInstance(this).create(SurveyService::class.java)
        service.getSurveyResult(seniorId, surveyId).enqueue(object : Callback<ApiResponse<SurveyResultData>> {
            override fun onResponse(
                call: Call<ApiResponse<SurveyResultData>>,
                response: Response<ApiResponse<SurveyResultData>>
            ) {
                binding.loadingLayout.visibility = View.GONE

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == 200) {
                        val data = body.data
                        pdfUrl = data.pdfUrl
                        riskLevel = data.riskLevel
                        summary = data.summary

                        binding.tvRiskLevel.text = "위험등급: $riskLevel"
                        binding.tvSummary.text = summary
                    } else {
                        Log.e("RiskAssessmentResult", "Fetch failed: status ${body?.status}")
                        showRetryDialog("결과 조회 실패. 다시 시도하시겠습니까?") { fetchSurveyResult(seniorId, surveyId) }
                    }
                } else {
                    Log.e("RiskAssessmentResult", "Server error: ${response.code()}")
                    showRetryDialog("서버 에러: ${response.code()}. 다시 시도하시겠습니까?") { fetchSurveyResult(seniorId, surveyId) }
                }
            }

            override fun onFailure(call: Call<ApiResponse<SurveyResultData>>, t: Throwable) {
                binding.loadingLayout.visibility = View.GONE
                Log.e("RiskAssessmentResult", "Network error: ${t.message}")
                showRetryDialog("네트워크 오류 발생. 다시 시도하시겠습니까?") { fetchSurveyResult(seniorId, surveyId) }
            }
        })
    }

    private fun downloadPdfUsingDownloadManager(pdfUrl: String, fileName: String) {
        try {
            val completeUrl = "https://api.happy-aging.co.kr/downloadPDF?filename=$pdfUrl"
            val uri = Uri.parse(completeUrl)
            val request = DownloadManager.Request(uri)
                .setTitle("Risk Assessment PDF")
                .setDescription("Downloading survey result PDF")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(this, "다운로드가 시작되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("RiskAssessmentResult", "Download error: ${e.message}", e)
            e.printStackTrace()
            Toast.makeText(this, "다운로드 시작 중 오류 발생", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRetryDialog(message: String, retryAction: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("오류")
            .setMessage(message)
            .setPositiveButton("재시도") { dialog, _ ->
                retryAction()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
