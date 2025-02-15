package com.winter.happyaging.ai

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.winter.happyaging.ResDTO.AIAnalysisResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "analysis_data")

class AnalysisDataStore(private val context: Context) {

    private val gson = Gson()
    private val ANALYSIS_RESULT_KEY = stringPreferencesKey("analysis_result")

    suspend fun saveAnalysisResult(response: AIAnalysisResponse) {
        val json = gson.toJson(response)
        context.dataStore.edit { preferences ->
            preferences[ANALYSIS_RESULT_KEY] = json
        }
    }

    fun getAnalysisResult(): Flow<AIAnalysisResponse?> {
        return context.dataStore.data.map { preferences ->
            preferences[ANALYSIS_RESULT_KEY]?.let { json ->
                gson.fromJson(json, AIAnalysisResponse::class.java)
            }
        }
    }

    suspend fun clearAnalysisData() {
        context.dataStore.edit { preferences ->
            preferences.remove(ANALYSIS_RESULT_KEY)
        }
    }
}
