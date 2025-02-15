package com.winter.happyaging

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("image_store")

class ImageManager(private val context: Context) {

    companion object {
        private val IMAGE_MAP_KEY = stringPreferencesKey("image_map")
    }

    data class ImageInfo(
        val roomName: String,
        val imageUrls: MutableList<String>
    )

    private var imageInfoMap: MutableMap<String, ImageInfo> = mutableMapOf()

    fun saveImageData(roomName: String, cameraIndex: Int, imageUrl: String) {
        GlobalScope.launch {
            context.dataStore.edit { preferences ->

                val existingData = getAllImageData()

                val roomData = existingData[roomName] ?: ImageInfo(roomName, mutableListOf("", "", ""))

                while (roomData.imageUrls.size <= cameraIndex) {
                    roomData.imageUrls.add("")
                }
                roomData.imageUrls[cameraIndex] = imageUrl

                imageInfoMap[roomName] = roomData
                preferences[IMAGE_MAP_KEY] = convertToPreferenceFormat()
            }
            Log.d("ImageManager", "이미지 저장됨: $roomName ($cameraIndex) -> $imageUrl")
        }
    }

    fun getImageData(roomName: String): List<String> {
        return getAllImageData()[roomName]?.imageUrls ?: emptyList()
    }

    private fun getAllImageData(): MutableMap<String, ImageInfo> {
        return runBlocking {
            context.dataStore.data.map { preferences ->
                val storedData = preferences[IMAGE_MAP_KEY]?.split("|") ?: emptyList()
                val tempMap = mutableMapOf<String, ImageInfo>()

                for (entry in storedData) {
                    val parts = entry.split("::")
                    if (parts.size == 2) {
                        val roomName = parts[0]
                        val imageUrls = parts[1].split(",").toMutableList()
                        tempMap[roomName] = ImageInfo(roomName, imageUrls)
                    }
                }

                tempMap
            }.first()
        }
    }

    private fun convertToPreferenceFormat(): String {
        return imageInfoMap.entries.joinToString("|") { entry ->
            "${entry.key}::${entry.value.imageUrls.joinToString(",")}"
        }
    }
}
