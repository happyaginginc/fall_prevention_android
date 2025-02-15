package com.winter.happyaging.ResDTO

data class AIAnalysisResponse(
    val status: Int,
    val data: RoomAIData
)

data class RoomAIData(
    val roomAIId: Int,
    val roomAIPrompts: List<RoomAIPrompt>
)

data class RoomAIPrompt(
    val roomAIPromptId: Int,
    val responseDto: ResponseDto,
    val roomCategory: String,
    val images: List<String>
)

data class ResponseDto(
    val imageDescription: String,
    val fallAnalysis: FallAnalysis,
    val fallSummaryDescription: String,
    val fallPreventionMeasures: List<String>
)

data class FallAnalysis(
    val obstacles: String,
    val floorCondition: String,
    val otherFactors: String
)
