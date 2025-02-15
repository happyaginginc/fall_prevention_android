package com.winter.happyaging.ResDTO

data class AIAnalysisRequest(
    val rooms: List<RoomRequest> // 방(Room) 목록을 포함하는 리스트
)

data class RoomRequest(
    val roomName: String,         // 방 이름
    val roomCategory: String,      // 방 카테고리
    val roomImages: List<String>   // 이미지 파일 이름 (백엔드에 업로드한 후 반환된 URL 또는 ID)
)
