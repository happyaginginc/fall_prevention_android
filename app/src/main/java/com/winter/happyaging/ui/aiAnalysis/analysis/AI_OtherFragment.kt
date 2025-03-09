package com.winter.happyaging.ui.aiAnalysis.analysis

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.AiAnalysisResponse
import com.winter.happyaging.data.aiAnalysis.model.RoomRequest
import com.winter.happyaging.data.aiAnalysis.repository.AiAnalysisRepository
import com.winter.happyaging.network.TokenManager
import kotlinx.coroutines.launch

class AI_OtherFragment : BaseRoomFragment(
    step = 6,
    roomType = "기타",
    nextAction = R.id.action_AIOtherFragment_to_AnalysisResultFragment
) {
    override val guideTexts: List<String>
        get() = listOf(
            "기타 공간(베란다, 외부 계단 등) 전체가 보이도록 촬영해주세요."
        )

    companion object {
        private const val TAG = "AI_OtherFragment"
        private const val ANALYSIS_DATA_PREFS = "AnalysisData"
        private const val ANALYSIS_RESULT_KEY = "analysisResult"
    }

    private lateinit var tokenManager: TokenManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenManager = TokenManager(requireContext())

        binding.btnNext.text = "분석 시작"
        binding.btnNext.setOnClickListener {
            sendAnalysisRequest()
        }
    }

    private fun sendAnalysisRequest() {
        val token = tokenManager.getAccessToken() ?: ""
        val seniorId = getStoredSeniorId()

        if (token.isEmpty() || seniorId == -1L) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val allRooms = imageManager.getAllImageData()
            Log.d(TAG, "모든 방의 이미지 데이터: $allRooms")

            val roomRequests: List<RoomRequest> = allRooms.values.mapNotNull { roomImageInfo ->
                val allImages = roomImageInfo.guides.flatten()
                if (allImages.isNotEmpty()) {
                    RoomRequest(
                        roomName = roomImageInfo.roomName,
                        roomCategory = getRoomCategory(roomImageInfo.roomName),
                        roomImages = allImages
                    )
                } else {
                    null
                }
            }

            if (roomRequests.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "업로드한 이미지가 없습니다.\n최소 1장 이상 등록해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            binding.btnPrev.visibility = View.GONE
            binding.btnNext.visibility = View.GONE
            binding.loadingOverlay.visibility = View.VISIBLE
            binding.scrollContainer.visibility = View.GONE

            AiAnalysisRepository.uploadRoomImages(
                context = requireContext(),
                binding = binding.loadingOverlay,
                seniorId = seniorId,
                roomRequests = roomRequests,
                onSuccess = { response ->
                    saveAnalysisResponse(response)
                    handleAnalysisSuccess()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "분석 실패: $error", Toast.LENGTH_SHORT).show()
                },
                onComplete = {
                    binding.loadingOverlay.visibility = View.GONE
                }
            )
        } catch (e: Exception) {
            binding.loadingOverlay.visibility = View.GONE
            Toast.makeText(requireContext(), "분석 요청 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAnalysisResponse(response: AiAnalysisResponse) {
        lifecycleScope.launch {
            try {
                val sharedPreferences = requireContext().getSharedPreferences(ANALYSIS_DATA_PREFS, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val json = Gson().toJson(response)
                editor.putString(ANALYSIS_RESULT_KEY, json)
                editor.apply()
                Log.d(TAG, "분석 결과 저장 완료")
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "분석 결과 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleAnalysisSuccess() {
        findNavController().navigate(R.id.action_AIOtherFragment_to_AnalysisResultFragment)
    }

    private fun getStoredSeniorId(): Long {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("seniorId", -1L)
    }

    private fun getRoomCategory(roomName: String): String {
        return when {
            roomName.contains("욕실") -> "BATHROOM"
            roomName.contains("화장실") -> "BATHROOM"
            roomName.contains("거실") -> "LIVING_ROOM"
            roomName.contains("주방") -> "KITCHEN"
            roomName.contains("침실") -> "GENERAL_ROOM"
            roomName.contains("외부") -> "OUTDOOR"
            else -> "OTHER"
        }
    }
}