package com.winter.happyaging.network

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore("image_store")

/**
 * 동적 가이드를 위해, roomName별로 여러 개의 가이드를 저장할 수 있는 구조
 * - guides 는 가이드 인덱스별로 이미지 리스트를 담고 있음
 *   예) guides[0] -> '가이드 #1' 의 이미지 목록
 *       guides[1] -> '가이드 #2' 의 이미지 목록
 *       ...
 */
data class RoomImageInfo(
    val roomName: String,
    val guides: MutableList<MutableList<String>> = mutableListOf()
)

/**
 * 기존 guide1, guide2, guide3 대신
 * - guides[0], guides[1], guides[2], ... 처럼 동적 인덱스로 접근
 */
class ImageManager(private val context: Context) {

    companion object {
        private val IMAGE_MAP_KEY = stringPreferencesKey("image_map")
        private const val TAG = "ImageManager"
    }

    // 별도의 코루틴 스코프(백그라운드) 사용
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * 이미지 저장
     * @param roomName    해당 방 이름 (ex: "욕실 1")
     * @param guideIndex  가이드 인덱스 (0-based가 편하지만, 기존 코드가 1부터 쓴다면 1->0 변환 가능)
     * @param imageUrl    실제 서버에 업로드된 이미지 파일명
     */
    fun saveImageData(roomName: String, guideIndex: Int, imageUrl: String) {
        scope.launch {
            val existingData = getAllImageData().toMutableMap()
            val roomData = existingData[roomName] ?: RoomImageInfo(roomName, mutableListOf())
            ensureGuideSize(roomData.guides, guideIndex)
            roomData.guides[guideIndex].add(imageUrl)
            existingData[roomName] = roomData

            // 직렬화하여 DataStore에 저장
            context.dataStore.edit { preferences ->
                preferences[IMAGE_MAP_KEY] = serializeImageData(existingData)
            }
            Log.d(TAG, "Saved image data: $roomName (guideIndex=$guideIndex) -> $imageUrl")
        }
    }

    /**
     * 특정 방의 이미지들 조회
     */
    fun getImageData(roomName: String): RoomImageInfo? = getAllImageData()[roomName]

    /**
     * 전체 방 + 가이드의 이미지들 조회
     */
    fun getAllImageData(): Map<String, RoomImageInfo> = runBlocking {
        context.dataStore.data.map { preferences ->
            val storedData = preferences[IMAGE_MAP_KEY] ?: ""
            deserializeImageData(storedData)
        }.first()
    }

    /**
     * 로컬 DataStore 초기화 (모든 저장 이미지 삭제)
     */
    fun clearLocalImageData() {
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences.remove(IMAGE_MAP_KEY)
            }
            Log.d(TAG, "Local image data cleared")
        }
    }

    /**
     * 직렬화: Map<String, RoomImageInfo> -> JSON String
     *   {
     *       "욕실 1": {
     *          "guides": [
     *              ["url1", "url2"],    // guideIndex=0
     *              ["url3"],            // guideIndex=1
     *              ...
     *          ]
     *       },
     *       "거실 1": {
     *          "guides": [ ... ]
     *       }
     *   }
     */
    private fun serializeImageData(data: Map<String, RoomImageInfo>): String {
        val jsonObj = JSONObject()
        data.forEach { (roomName, roomInfo) ->
            val roomJson = JSONObject().apply {
                val guideArray = JSONArray()
                roomInfo.guides.forEach { images ->
                    guideArray.put(JSONArray(images))
                }
                put("guides", guideArray)
            }
            jsonObj.put(roomName, roomJson)
        }
        return jsonObj.toString()
    }

    /**
     * 역직렬화: JSON String -> Map<String, RoomImageInfo>
     */
    private fun deserializeImageData(serializedData: String): Map<String, RoomImageInfo> {
        if (serializedData.isBlank()) return emptyMap()

        val result = mutableMapOf<String, RoomImageInfo>()
        val jsonObj = JSONObject(serializedData)

        jsonObj.keys().forEach { roomName ->
            val roomJson = jsonObj.getJSONObject(roomName)
            val guidesList = mutableListOf<MutableList<String>>()

            val guideArray = roomJson.getJSONArray("guides")
            for (i in 0 until guideArray.length()) {
                val imagesJson = guideArray.getJSONArray(i)
                val images = mutableListOf<String>()
                for (j in 0 until imagesJson.length()) {
                    images.add(imagesJson.getString(j))
                }
                guidesList.add(images)
            }

            result[roomName] = RoomImageInfo(roomName, guidesList)
            Log.d(TAG, "Restored data for room: $roomName")
        }
        return result
    }

    /**
     * guideIndex에 맞게 guides 리스트 크기를 늘려주는 메서드
     * 예) guideIndex=3인데 guides.size=2라면, guides[0], guides[1], guides[2], guides[3]까지 필요
     */
    private fun ensureGuideSize(guides: MutableList<MutableList<String>>, guideIndex: Int) {
        while (guides.size <= guideIndex) {
            guides.add(mutableListOf())
        }
    }
}