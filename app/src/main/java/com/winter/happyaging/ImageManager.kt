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

class ImageManager(private val context: Context) {

    companion object {
        private val IMAGE_MAP_KEY = stringPreferencesKey("image_map")
    }

    data class ImageInfo(
        val roomName: String,
        val imageUrls: MutableList<String>
    )

    // DataStore에 이미지 저장
    fun saveImageData(roomName: String, cameraIndex: Int, imageUrl: String) {
        GlobalScope.launch {
            context.dataStore.edit { preferences ->
                val existingData = getAllImageData()

                // 기존 데이터 가져오고 없으면 새로 생성
                val roomData = existingData[roomName] ?: ImageInfo(roomName, mutableListOf("", "", ""))

                // 크기 맞추기
                while (roomData.imageUrls.size <= cameraIndex) {
                    roomData.imageUrls.add("")
                }
                roomData.imageUrls[cameraIndex] = imageUrl // 이미지 추가

                // DataStore에 저장할 직렬화된 데이터 생성
                val newMap = existingData.toMutableMap().apply { put(roomName, roomData) }
                preferences[IMAGE_MAP_KEY] = serializeImageData(newMap)

                // 저장된 데이터 확인
                Log.d("ImageManager", "현재 저장된 이미지 데이터: ${preferences[IMAGE_MAP_KEY]}")
            }
            Log.d("ImageManager", "이미지 저장됨: $roomName ($cameraIndex) -> $imageUrl")
        }
    }

    // ✅ 2️⃣ DataStore에서 이미지 불러오기
    fun getImageData(roomName: String): List<String> {
        return getAllImageData()[roomName]?.imageUrls ?: emptyList()
    }

    // ✅ 3️⃣ DataStore에서 모든 이미지 데이터 불러오기
    fun getAllImageData(): Map<String, ImageInfo> {
        return runBlocking {
            context.dataStore.data.map { preferences ->
                val storedData = preferences[IMAGE_MAP_KEY] ?: ""
                deserializeImageData(storedData)
            }.first()
        }
    }

    // ✅ 4️⃣ 데이터를 직렬화하여 DataStore에 저장하기
    private fun serializeImageData(data: Map<String, ImageInfo>): String {
        return data.entries.joinToString("|") { entry ->
            "${entry.key}::${entry.value.imageUrls.joinToString(",")}"
        }
    }

    // ✅ 5️⃣ DataStore에서 가져온 문자열을 다시 변환
    private fun deserializeImageData(serializedData: String): Map<String, ImageInfo> {
        if (serializedData.isBlank()) return emptyMap()

        return serializedData.split("|").mapNotNull { entry ->
            val parts = entry.split("::")
            if (parts.size == 2) {
                val roomName = parts[0]
                val imageUrls = parts[1].split(",").toMutableList()
                roomName to ImageInfo(roomName, imageUrls)
            } else {
                null
            }
        }.toMap()
    }

    fun clearLocalImageData() {
        GlobalScope.launch {
            context.dataStore.edit { preferences ->
                preferences.remove(IMAGE_MAP_KEY)  // 기존에 저장된 이미지 데이터 삭제
            }
            Log.d("ImageManager", "로컬 이미지 데이터 초기화 완료!")
        }
    }
}
