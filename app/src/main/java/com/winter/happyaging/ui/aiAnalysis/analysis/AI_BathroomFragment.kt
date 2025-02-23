package com.winter.happyaging.ui.aiAnalysis.analysis

import com.winter.happyaging.R

class AI_BathroomFragment : BaseRoomFragment(
    step = 1,
    roomType = "욕실/화장실",
    nextAction = R.id.action_AIBathroomFragment_to_AILivingRoomFragment
) {
    override val guideTexts: List<String>
        get() = listOf(
            "욕실 내부 전체가 보이도록 촬영해주세요.",
            "욕실 바닥재가 보이도록 촬영해주세요.",
            "변기, 세면대가 보이도록 촬영해주세요.",
            "욕실 문턱(단차)이 보이도록 촬영해주세요."
        )
}