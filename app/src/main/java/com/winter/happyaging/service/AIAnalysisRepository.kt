package com.winter.happyaging.service

import android.content.Context
import android.util.Log
import android.view.View
import com.winter.happyaging.R
import com.winter.happyaging.ResDTO.AIAnalysisRequest
import com.winter.happyaging.ResDTO.AIAnalysisResponse
import com.winter.happyaging.ResDTO.RoomRequest
import com.winter.happyaging.RetrofitClient
import com.winter.happyaging.ai.RoomData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AIAnalysisRepository {

    fun uploadRoomImages(
        context: Context,
        binding: View, // 로딩 화면 조작을 위해 추가
        seniorId: Int,
        token: String,
        roomDataList: List<RoomData>,
        onSuccess: (AIAnalysisResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val service = RetrofitClient.getInstance(context).create(AIAnalysisService::class.java)

        val rooms = roomDataList.map { room ->
            RoomRequest(
                roomName = room.name, // RoomAdapter의 roomName을 API의 roomName으로 전달
                roomCategory = getRoomCategory(room.name),
                roomImages = listOf() // ImageManager에서 데이터 가져와야 함
            )
        }

        // 로딩 화면 표시
        binding.findViewById<View>(R.id.loadingLayout)?.visibility = View.VISIBLE

        val request = AIAnalysisRequest(rooms)

        Log.d("AIAnalysis", "요청 데이터 : $request")
        service.analysisImages(seniorId, "Bearer $token", request)
            .enqueue(object : Callback<AIAnalysisResponse> {
                override fun onResponse(call: Call<AIAnalysisResponse>, response: Response<AIAnalysisResponse>) {
                    // 로딩 화면 숨김
                    binding.findViewById<View>(R.id.loadingLayout)?.visibility = View.GONE

                    Log.d("seniorId","$seniorId")

                    if (response.isSuccessful) {
                        Log.d("AIAnalysis", "업로드 성공: ${response.body()}")
                        response.body()?.let {
                            Log.d("AIAnalysis", "분석 결과: ${it.data}")
                            onSuccess(it)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("AIAnalysis", "업로드 실패: $errorBody")
                        onFailure("서버 응답 오류: $errorBody")
                    }
                }

                override fun onFailure(call: Call<AIAnalysisResponse>, t: Throwable) {
                    binding.findViewById<View>(R.id.loadingLayout)?.visibility = View.GONE
                    Log.e("AIAnalysis", "네트워크 오류 발생", t)
                    onFailure("네트워크 오류")
                }
            })
    }

    private fun getRoomCategory(roomName: String): String {
        return when {
            roomName.contains("화장실") -> "BATHROOM"
            roomName.contains("방") -> "ROOM"
            else -> "OTHER"
        }
    }
}
