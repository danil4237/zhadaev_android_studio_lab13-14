package com.zhadaev.unscramblegame.ui_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.zhadaev.unscramblegame.data.GameUiState
import com.zhadaev.unscramblegame.data.MAX_NO_OF_WORDS
import com.zhadaev.unscramblegame.data.SCORE_INCREASE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.zhadaev.unscramblegame.data.allWords
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    fun resetGame(){
        usedWords.clear()
        _uiState.value = GameUiState(
            currentScrambleWord = pickRandomWordAndShuffle()
        )
    }

    private fun shuffleCurrentWord(word: String): String{
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (String(tempWord) == word){
            tempWord.shuffle()
        }
        return String(tempWord)
    }


    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        while (usedWords.contains(currentWord)){
            currentWord = allWords.random()
        }
        usedWords.add(currentWord)
        return shuffleCurrentWord(currentWord)
    }

    fun upadteUserGuess(guessWord: String){
        userGuess = guessWord
    }

    private fun updateGameState(updateScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updateScore,
                    isGameOver = true
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambleWord = pickRandomWordAndShuffle(),
                    score = updateScore,
                    currentWordCount = currentState.currentWordCount + 1
                )
            }
        }
    }


    fun checkUserGuess(){
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score + SCORE_INCREASE
            updateGameState(updatedScore)
        } else{
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("") //32 страница
    }




}