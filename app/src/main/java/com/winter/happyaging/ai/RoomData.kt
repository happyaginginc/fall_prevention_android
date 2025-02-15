package com.winter.happyaging.ai

import android.net.Uri

data class RoomData(
    var name: String,      // 방 이름
    var imageUri1: Uri? = null,
    var imageUri2: Uri? = null,
    var imageUri3: Uri? = null
)