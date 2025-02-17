package com.winter.happyaging.ui.aiAnalysis

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
import kotlinx.coroutines.launch

class AIFourthFragment :
    BaseRoomFragment(
        3,
        "기타",
        R.id.action_AIFourthFragment_to_AnalysisResultFragment
    ) {
    private lateinit var tokenManager: TokenManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenManager = TokenManager(requireContext())

        binding.nextButton.text = "분석 시작"
        binding.nextButton.setOnClickListener {
            sendAnalysisRequest()
        }

        loadStoredImages()
    }

    private fun loadStoredImages() {
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
            }
        )
    }

    private fun saveAnalysisResponse(response: AiAnalysisResponse) {
        lifecycleScope.launch {
            val sharedPreferences = requireContext().getSharedPreferences("AnalysisData", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(response)
            editor.putString("analysisResult", json)
            editor.apply()
            Log.d("AIFourthFragment", "분석 결과 저장 완료")
        }
    }

    private fun handleAnalysisSuccess() {
        findNavController().navigate(R.id.action_AIFourthFragment_to_AnalysisResultFragment)
    }

    private fun getStoredSeniorId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("seniorId", -1)
    }

    private fun getRoomCategory(roomName: String): String {
        return when {
            roomName.contains("화장실") -> "BATHROOM"
            roomName.contains("방") -> "GENERAL_ROOM"
            else -> "OTHER"
        }
    }
}
