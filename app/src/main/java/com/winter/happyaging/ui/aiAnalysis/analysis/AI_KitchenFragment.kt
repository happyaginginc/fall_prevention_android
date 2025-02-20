package com.winter.happyaging.ui.aiAnalysis.analysis

import com.winter.happyaging.R
import com.winter.happyaging.ui.aiAnalysis.BaseRoomFragment

class AI_KitchenFragment : BaseRoomFragment(
    step = 2,
    roomType = "주방",
    nextAction = R.id.action_AIKitchenFragment_to_AIBathroomFragment
) {
    override val guideText1: String
        get() = "주방 전체를 촬영해주세요. (요리 공간, 싱크대, 식탁 포함)"
    override val guideText2: String
        get() = "조리 도구나 작업대의 상태가 잘 보이도록 촬영해주세요."
    override val guideText3: String
        get() = "환기가 잘 되는지 창문이나 환풍구도 함께 담아주세요."
}