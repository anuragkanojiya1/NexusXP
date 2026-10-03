package com.game.arodyssey

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.scoreDataStore by preferencesDataStore(name = "score_preferences")

class ScoreDataStoreManager(private val context: Context) {
    companion object {
        private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
        private val CURRENT_SCORE_KEY = intPreferencesKey("current_score")
    }

    val highScoreFlow: Flow<Int> = context.scoreDataStore.data
        .map { preferences ->
            preferences[HIGH_SCORE_KEY] ?: 0
        }

    val currentScoreFlow: Flow<Int> = context.scoreDataStore.data
        .map { preferences ->
            preferences[CURRENT_SCORE_KEY] ?: 0
        }

    suspend fun saveCurrentScore(score: Int) {
        context.scoreDataStore.edit { preferences ->
            preferences[CURRENT_SCORE_KEY] = score
            val existingHighScore = preferences[HIGH_SCORE_KEY] ?: 0
            if (score > existingHighScore) {
                preferences[HIGH_SCORE_KEY] = score
            }
        }
    }
}
