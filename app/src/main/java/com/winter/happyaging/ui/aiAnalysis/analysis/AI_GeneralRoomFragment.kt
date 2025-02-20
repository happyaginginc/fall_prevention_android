package com.winter.happyaging.ui.aiAnalysis.analysis

import com.winter.happyaging.R

class AI_GeneralRoomFragment : BaseRoomFragment(
    step = 4,
    roomType = "침실",
    nextAction = R.id.action_AIGeneralRoomFragment_to_AIOutdoorFragment
) {
    override val guideText1: String
        get() = "방 전체를 촬영해주세요. (침대, 책상 등 주요 가구 포함)"
    override val guideText2: String
        get() = "실내 인테리어와 소품이 잘 보이도록 촬영해주세요."
    override val guideText3: String
        get() = "자연광을 활용해 방의 분위기를 담아주세요."
}