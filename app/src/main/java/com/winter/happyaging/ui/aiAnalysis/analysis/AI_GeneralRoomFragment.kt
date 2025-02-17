package com.winter.happyaging.ui.aiAnalysis.analysis

import com.winter.happyaging.R
import com.winter.happyaging.ui.aiAnalysis.BaseRoomFragment

class AI_GeneralRoomFragment : BaseRoomFragment(
    step = 4,
    roomType = "일반 방",
    nextAction = R.id.action_AIGeneralRoomFragment_to_AIOutdoorFragment
)