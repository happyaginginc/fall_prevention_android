package com.winter.happyaging.ui.aiAnalysis.analysis

import com.winter.happyaging.R

class AI_KitchenFragment : BaseRoomFragment(
    step = 2,
    roomType = "주방",
    nextAction = R.id.action_AIKitchenFragment_to_AIGeneralRoomFragment
) {
    override val guideTexts: List<String>
        get() = listOf(
            "주방 전체가 잘 보이도록 촬영해주세요. (요리 공간, 싱크대, 식탁 포함)",
            "주방 바닥재가 잘 보이도록 촬영해주세요."
        )
}