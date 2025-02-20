package com.winter.happyaging.ui.aiAnalysis.analysis

import com.winter.happyaging.R
import com.winter.happyaging.ui.aiAnalysis.BaseRoomFragment

class AI_OutdoorFragment : BaseRoomFragment(
    step = 5,
    roomType = "외부",
    nextAction = R.id.action_AIOutdoorFragment_to_AIOtherFragment
) {
    override val guideText1: String
        get() = "외부 전체 환경을 촬영해주세요. (정원, 마당 등 포함)"
    override val guideText2: String
        get() = "건물 외관과 주변 경관이 잘 보이도록 촬영해주세요."
    override val guideText3: String
        get() = "넓은 시야를 확보하여 외부 공간의 특성을 담아주세요."
}