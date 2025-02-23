package com.winter.happyaging.ui.aiAnalysis.analysis

import com.winter.happyaging.R

class AI_OutdoorFragment : BaseRoomFragment(
    step = 5,
    roomType = "외부",
    nextAction = R.id.action_AIOutdoorFragment_to_AIOtherFragment
) {
    override val guideTexts: List<String>
        get() = listOf(
            "현관(출입구) 전체가 보이도록 촬영해주세요.",
            "바닥재가 잘 보이도록 촬영해주세요.",
            "현관(문턱, 단차) 전체가 보이도록 촬영해주세요."
        )
}