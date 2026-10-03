package com.game.arodyssey

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.modelPreferencesDataStore by preferencesDataStore(name = "model_preferences")

class PreferencesManager(private val context: Context) {

    fun getModelStateFlow(modelId: String): Flow<String> {
        val key = stringPreferencesKey(modelId)
        return context.modelPreferencesDataStore.data
            .map { preferences ->
                preferences[key] ?: "locked"
            }
    }

    val allModelsStateFlow: Flow<Map<String, String>> = context.modelPreferencesDataStore.data
        .map { preferences ->
            val modelIds = listOf("0", "1", "2", "3", "4")
            modelIds.associateWith { id ->
                preferences[stringPreferencesKey(id)] ?: "locked"
            }
        }

    suspend fun getModelState(modelId: String): String {
        val key = stringPreferencesKey(modelId)
        return context.modelPreferencesDataStore.data
            .map { preferences ->
                preferences[key] ?: "locked"
            }.first()
    }

    suspend fun saveModelState(modelId: String, state: String) {
        val key = stringPreferencesKey(modelId)
        context.modelPreferencesDataStore.edit { preferences ->
            preferences[key] = state
        }
    }

    suspend fun fetchAllModelsState(): Map<String, String> {
        val modelIds = listOf("0", "1", "2", "3", "4")
        return modelIds.associateWith { getModelState(it) }
    }
}
