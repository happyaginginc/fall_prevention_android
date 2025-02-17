package com.winter.happyaging.ui.aiAnalysis.analysis

import android.content.Context
import android.net.Uri
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
import com.winter.happyaging.ui.aiAnalysis.BaseRoomFragment
import kotlinx.coroutines.launch

class AI_OtherFragment : BaseRoomFragment(
    step = 6,
    roomType = "기타",
    nextAction = R.id.action_AIOtherFragment_to_AnalysisResultFragment
) {
    companion object {
        private const val TAG = "AI_OtherFragment"
        private const val ANALYSIS_DATA_PREFS = "AnalysisData"
        private const val ANALYSIS_RESULT_KEY = "analysisResult"
    }

    private lateinit var tokenManager: TokenManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenManager = TokenManager(requireContext())

        // "기타"는 마지막 단계이므로 '다음' 대신 '분석 시작'으로 텍스트 변경
        binding.nextButton.text = "분석 시작"
        binding.nextButton.setOnClickListener {
            sendAnalysisRequest()
        }

        loadStoredImages()
    }

    /**
     * BaseRoomFragment의 기본 기능(방/이미지) + 서버 분석 요청 로직
     */

    private fun loadStoredImages() {
        // 기존에 저장된 로컬 이미지를 복원하여 미리보기 갱신
        for (room in roomList) {
            val storedImages = imageManager.getImageData(room.name)
            room.imageUri1 = storedImages.getOrNull(0)?.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
            room.imageUri2 = storedImages.getOrNull(1)?.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
            room.imageUri3 = storedImages.getOrNull(2)?.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
        }
        binding.roomRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun sendAnalysisRequest() {
        val token = tokenManager.getAccessToken() ?: ""
        val seniorId = getStoredSeniorId()

        if (token.isEmpty() || seniorId == -1) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val allRooms = imageManager.getAllImageData()
        val roomRequests: List<RoomRequest> = allRooms.values.map { room ->
            RoomRequest(
                roomName = room.roomName,
                roomCategory = getRoomCategory(room.roomName),
                roomImages = room.imageUrls.filter { it.isNotEmpty() }
            )
        }

        // 로딩 표시
        binding.loadingLayout.visibility = View.VISIBLE
        binding.mainContent.visibility = View.GONE

        AiAnalysisRepository.uploadRoomImages(
            context = requireContext(),
            binding = binding.loadingLayout,
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
                // 로딩 해제
                binding.loadingLayout.visibility = View.GONE
                binding.mainContent.visibility = View.VISIBLE
            }
        )
    }

    private fun saveAnalysisResponse(response: AiAnalysisResponse) {
        lifecycleScope.launch {
            val sharedPreferences = requireContext().getSharedPreferences(ANALYSIS_DATA_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val json = Gson().toJson(response)
            editor.putString(ANALYSIS_RESULT_KEY, json)
            editor.apply()
            Log.d(TAG, "분석 결과 저장 완료")
        }
    }

    private fun handleAnalysisSuccess() {
        // 분석 완료 시 결과 화면으로 이동
        findNavController().navigate(R.id.action_AIOtherFragment_to_AnalysisResultFragment)
    }

    private fun getStoredSeniorId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("seniorId", -1)
    }

    private fun getRoomCategory(roomName: String): String {
        return when {
            roomName.contains("욕실") -> "BATHROOM"
            roomName.contains("화장실") -> "BATHROOM"
            roomName.contains("거실") -> "LIVING_ROOM"
            roomName.contains("주방") -> "KITCHEN"
            roomName.contains("방") -> "GENERAL_ROOM"
            roomName.contains("외부") -> "OUTDOOR"
            else -> "OTHER"
        }
    }

    // BaseRoomFragment의 onNextButtonClick은 사용하지 않음
    override fun onNextButtonClick() {
        // 마지막 스텝이므로 별도 처리
        sendAnalysisRequest()
    }
}