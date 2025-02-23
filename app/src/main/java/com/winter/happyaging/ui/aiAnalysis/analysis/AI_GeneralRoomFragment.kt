package com.winter.happyaging.ui.aiAnalysis.analysis

import com.winter.happyaging.R

class AI_GeneralRoomFragment : BaseRoomFragment(
    step = 4,
    roomType = "침실",
    nextAction = R.id.action_AIGeneralRoomFragment_to_AIOutdoorFragment
) {
    override val guideTexts: List<String>
        get() = listOf(
            "방 전체를 촬영해주세요. (침대, 책상 등 주요 가구 포함)",
            "침실 바닥이 잘 보이도록 촬영해주세요.",
            "스위치 모양, 침대와 스위치까지 거리가 보이도록 촬영해주세요."
        )
}