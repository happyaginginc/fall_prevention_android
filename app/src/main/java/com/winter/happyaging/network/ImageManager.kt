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

data class RoomImageInfo(
    val roomName: String,
    val guides: MutableList<MutableList<String>> = mutableListOf()
)

class ImageManager(private val context: Context) {

    companion object {
        private val IMAGE_MAP_KEY = stringPreferencesKey("image_map")
        private const val TAG = "ImageManager"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * 이미지 URL을 roomName/guideIndex별로 저장
     */
    fun saveImageData(roomName: String, guideIndex: Int, imageUrl: String) {
        scope.launch {
            val existingData = getAllImageData().toMutableMap()
            val roomData = existingData[roomName] ?: RoomImageInfo(roomName, mutableListOf())

            ensureGuideSize(roomData.guides, guideIndex)
            roomData.guides[guideIndex].add(imageUrl)

            existingData[roomName] = roomData

            context.dataStore.edit { preferences ->
                preferences[IMAGE_MAP_KEY] = serializeImageData(existingData)
            }
            Log.d(TAG, "Saved image data: $roomName (guideIndex=$guideIndex) -> $imageUrl")
        }
    }

    /**
     * 해당 roomName, guideIndex에 저장된 이미지 중 imageUrl 제거
     */
    fun removeImageData(roomName: String, guideIndex: Int, imageUrl: String) {
        scope.launch {
            val existingData = getAllImageData().toMutableMap()
            val roomData = existingData[roomName]

            if (roomData != null && guideIndex < roomData.guides.size) {
                roomData.guides[guideIndex].remove(imageUrl)
            }
            existingData[roomName] = roomData ?: RoomImageInfo(roomName, mutableListOf())

            context.dataStore.edit { preferences ->
                preferences[IMAGE_MAP_KEY] = serializeImageData(existingData)
            }
            Log.d(TAG, "Removed image data: $roomName (guideIndex=$guideIndex) -> $imageUrl")
        }
    }

    fun getImageData(roomName: String): RoomImageInfo? = getAllImageData()[roomName]

    fun getAllImageData(): Map<String, RoomImageInfo> = runBlocking {
        context.dataStore.data.map { preferences ->
            val storedData = preferences[IMAGE_MAP_KEY] ?: ""
            deserializeImageData(storedData)
        }.first()
    }

    /**
     * 로컬(DataStore)에 있는 모든 이미지 데이터 초기화
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
     * 맵 -> JSON 직렬화
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
     * JSON -> 맵 역직렬화
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
        }
        return result
    }

    /**
     * guideIndex까지 guides가 존재하지 않을 경우 확장
     */
    private fun ensureGuideSize(guides: MutableList<MutableList<String>>, guideIndex: Int) {
        while (guides.size <= guideIndex) {
            guides.add(mutableListOf())
        }
    }
}
