package com.winter.happyaging.ui.aiAnalysis.analysis

import com.winter.happyaging.R

class AI_LivingRoomFragment : BaseRoomFragment(
    step = 1,
    roomType = "거실",
    nextAction = R.id.action_AILivingRoomFragment_to_AIKitchenFragment
) {
    override val guideText1: String
        get() = "욕실 내부 전체를 밝게 촬영해주세요."
    override val guideText2: String
        get() = "세면대와 거울이 보이도록 촬영해주세요."
    override val guideText3: String
        get() = "문을 열어 환기가 잘 되는 모습을 담아주세요."
}