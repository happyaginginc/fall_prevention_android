package com.winter.happyaging

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// DataStore 선언
private val Context.dataStore by preferencesDataStore("image_store")

data class RoomImageInfo(
    val roomName: String,
    val guide1: MutableList<String> = mutableListOf(),
    val guide2: MutableList<String> = mutableListOf(),
    val guide3: MutableList<String> = mutableListOf()
)

class ImageManager(private val context: Context) {

    companion object {
        private val IMAGE_MAP_KEY = stringPreferencesKey("image_map")
        // 구분자: 각 방은 "|" 로, 각 가이드는 "||" 로, 이미지들은 "," 로 구분
    }

    // DataStore에 이미지 저장 (guideIndex: 1, 2, 3)
    fun saveImageData(roomName: String, guideIndex: Int, imageUrl: String) {
        GlobalScope.launch {
            context.dataStore.edit { preferences ->
                val existingData = getAllImageData().toMutableMap()
                val roomData = existingData[roomName] ?: RoomImageInfo(roomName)
                when (guideIndex) {
                    1 -> roomData.guide1.add(imageUrl)
                    2 -> roomData.guide2.add(imageUrl)
                    3 -> roomData.guide3.add(imageUrl)
                }
                existingData[roomName] = roomData
                preferences[IMAGE_MAP_KEY] = serializeImageData(existingData)
                Log.d("ImageManager", "현재 저장된 이미지 데이터: ${preferences[IMAGE_MAP_KEY]}")
            }
            Log.d("ImageManager", "이미지 저장됨: $roomName (guide $guideIndex) -> $imageUrl")
        }
    }

    // DataStore에서 특정 방의 이미지 정보를 가져오기
    fun getImageData(roomName: String): RoomImageInfo? {
        return getAllImageData()[roomName]
    }

    // DataStore에서 모든 이미지 데이터 불러오기
    fun getAllImageData(): Map<String, RoomImageInfo> {
        return runBlocking {
            context.dataStore.data.map { preferences ->
                val storedData = preferences[IMAGE_MAP_KEY] ?: ""
                deserializeImageData(storedData)
            }.first()
        }
    }

    // 데이터를 직렬화하여 DataStore에 저장하기
    private fun serializeImageData(data: Map<String, RoomImageInfo>): String {
        // 각 방: roomName::guide1Img1,guide1Img2||guide2Img1,guide2Img2||guide3Img1,guide3Img2
        return data.entries.joinToString("|") { entry ->
            val room = entry.value
            val guide1Str = room.guide1.joinToString(",")
            val guide2Str = room.guide2.joinToString(",")
            val guide3Str = room.guide3.joinToString(",")
            "${entry.key}::${guide1Str}||${guide2Str}||${guide3Str}"
        }
    }

    // DataStore에서 가져온 문자열을 다시 변환
    private fun deserializeImageData(serializedData: String): Map<String, RoomImageInfo> {
        if (serializedData.isBlank()) return emptyMap()
        return serializedData.split("|").mapNotNull { entry ->
            val parts = entry.split("::")
            if (parts.size == 2) {
                val roomName = parts[0]
                val guides = parts[1].split("||")
                if (guides.size == 3) {
                    val guide1 = if (guides[0].isNotBlank()) guides[0].split(",").toMutableList() else mutableListOf()
                    val guide2 = if (guides[1].isNotBlank()) guides[1].split(",").toMutableList() else mutableListOf()
                    val guide3 = if (guides[2].isNotBlank()) guides[2].split(",").toMutableList() else mutableListOf()
                    roomName to RoomImageInfo(roomName, guide1, guide2, guide3)
                } else null
            } else null
        }.toMap()
    }

    fun clearLocalImageData() {
        GlobalScope.launch {
            context.dataStore.edit { preferences ->
                preferences.remove(IMAGE_MAP_KEY)
            }
            Log.d("ImageManager", "로컬 이미지 데이터 초기화 완료!")
        }
    }
}