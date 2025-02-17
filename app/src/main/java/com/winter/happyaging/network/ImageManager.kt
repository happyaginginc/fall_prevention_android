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
    val guide1: MutableList<String> = mutableListOf(),
    val guide2: MutableList<String> = mutableListOf(),
    val guide3: MutableList<String> = mutableListOf()
)

class ImageManager(private val context: Context) {

    companion object {
        private val IMAGE_MAP_KEY = stringPreferencesKey("image_map")
        private const val TAG = "ImageManager"
    }

    // 별도의 코루틴 스코프(백그라운드) 사용
    private val scope = CoroutineScope(Dispatchers.IO)

    fun saveImageData(roomName: String, guideIndex: Int, imageUrl: String) {
        scope.launch {
            val existingData = getAllImageData().toMutableMap()
            val roomData = existingData[roomName] ?: RoomImageInfo(roomName)
            when (guideIndex) {
                1 -> roomData.guide1.add(imageUrl)
                2 -> roomData.guide2.add(imageUrl)
                3 -> roomData.guide3.add(imageUrl)
            }
            existingData[roomName] = roomData
            context.dataStore.edit { preferences ->
                preferences[IMAGE_MAP_KEY] = serializeImageData(existingData)
            }
            Log.d(TAG, "Saved image data: $roomName (guide $guideIndex) -> $imageUrl")
        }
    }

    fun getImageData(roomName: String): RoomImageInfo? = getAllImageData()[roomName]

    fun getAllImageData(): Map<String, RoomImageInfo> = runBlocking {
        context.dataStore.data.map { preferences ->
            val storedData = preferences[IMAGE_MAP_KEY] ?: ""
            deserializeImageData(storedData)
        }.first()
    }

    private fun serializeImageData(data: Map<String, RoomImageInfo>): String {
        val jsonObj = JSONObject()
        data.forEach { (roomName, roomInfo) ->
            val roomJson = JSONObject().apply {
                put("guide1", JSONArray(roomInfo.guide1))
                put("guide2", JSONArray(roomInfo.guide2))
                put("guide3", JSONArray(roomInfo.guide3))
            }
            jsonObj.put(roomName, roomJson)
        }
        return jsonObj.toString()
    }

    private fun deserializeImageData(serializedData: String): Map<String, RoomImageInfo> {
        if (serializedData.isBlank()) return emptyMap()
        val result = mutableMapOf<String, RoomImageInfo>()
        val jsonObj = JSONObject(serializedData)
        jsonObj.keys().forEach { roomName ->
            val roomJson = jsonObj.getJSONObject(roomName)
            val guide1 = mutableListOf<String>()
            val guide2 = mutableListOf<String>()
            val guide3 = mutableListOf<String>()

            val guide1Array = roomJson.getJSONArray("guide1")
            val guide2Array = roomJson.getJSONArray("guide2")
            val guide3Array = roomJson.getJSONArray("guide3")

            for (i in 0 until guide1Array.length()) guide1.add(guide1Array.getString(i))
            for (i in 0 until guide2Array.length()) guide2.add(guide2Array.getString(i))
            for (i in 0 until guide3Array.length()) guide3.add(guide3Array.getString(i))

            result[roomName] = RoomImageInfo(roomName, guide1, guide2, guide3)
            Log.d(TAG, "Restored data for room: $roomName")
        }
        return result
    }

    fun clearLocalImageData() {
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences.remove(IMAGE_MAP_KEY)
            }
            Log.d(TAG, "Local image data cleared")
        }
    }
}
