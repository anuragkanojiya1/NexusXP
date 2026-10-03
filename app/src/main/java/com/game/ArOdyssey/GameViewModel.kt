package com.game.arodyssey

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigInteger

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = ScoreDataStoreManager(application)

    val highScore: StateFlow<Int> = dataStoreManager.highScoreFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _playerScore = MutableStateFlow<BigInteger>(BigInteger.ZERO)
    val playerScore: StateFlow<BigInteger> get() = _playerScore
    var score = mutableStateOf(0)

    fun updatePlayerScore(newScore: BigInteger) {
        _playerScore.value = newScore
    }

    fun saveScore(newScore: Int) {
        score.value = newScore
        viewModelScope.launch {
            dataStoreManager.saveCurrentScore(newScore)
        }
    }
}
