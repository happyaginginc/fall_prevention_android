package com.winter.happyaging.ui.aiAnalysis.analysis

import com.winter.happyaging.R

class AI_LivingRoomFragment : BaseRoomFragment(
    step = 2,
    roomType = "거실",
    nextAction = R.id.action_AILivingRoomFragment_to_AIKitchenFragment
) {
    override val guideTexts: List<String>
        get() = listOf(
            "거실 전체가 잘 보이도록 촬영해주세요.",
            "거실 바닥재가 잘 보이도록 촬영해주세요."
        )
}