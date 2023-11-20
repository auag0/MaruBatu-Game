package com.anago.marubatu.ui.marubatu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anago.marubatu.models.FieldSymbol
import com.anago.marubatu.models.FieldSymbol.CPU
import com.anago.marubatu.models.FieldSymbol.ME
import com.anago.marubatu.models.FieldSymbol.NONE
import kotlin.random.Random

class MaruBatuViewModel : ViewModel() {
    private var _turn: MutableLiveData<FieldSymbol> = MutableLiveData(NONE)
    val turn: LiveData<FieldSymbol> = _turn

    private var _fields: MutableLiveData<Array<FieldSymbol>> = MutableLiveData(emptyArray())
    val fields: LiveData<Array<FieldSymbol>> = _fields

    private var _isFinished: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFinished: LiveData<Boolean> = _isFinished

    private var _winnerSymbol: MutableLiveData<FieldSymbol?> = MutableLiveData(null)
    val winnerSymbol: LiveData<FieldSymbol?> = _winnerSymbol

    private var _connectedFieldPos: MutableLiveData<List<Int>> = MutableLiveData(emptyList())
    val connectedFieldPos: LiveData<List<Int>> = _connectedFieldPos

    /*
    0 1 2
    3 4 5
    6 7 8
     */
    private val checkPattern = arrayOf(
        // 縦
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        // 横
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        // 斜め
        intArrayOf(0, 4, 8),
        intArrayOf(2, 4, 6)
    )

    private fun initializeGame() {
        setRandomTurn()
        setNoneFields()
    }

    private fun setFieldSymbol(position: Int, symbol: FieldSymbol) {
        _fields.value = _fields.value?.apply {
            set(position, symbol)
        }
    }

    private fun setRandomTurn() {
        _turn.value = if (Random.nextBoolean()) {
            ME
        } else {
            CPU
        }
    }

    private fun isFieldNone(pos: Int): Boolean {
        return _fields.value?.get(pos) == NONE
    }

    private fun getCurrentTurnSymbol(): FieldSymbol {
        return _turn.value ?: NONE
    }

    fun handleFieldClick(pos: Int) {
        if (_isFinished.value == false) {
            if (isFieldNone(pos)) {
                setFieldSymbol(pos, getCurrentTurnSymbol())
                toggleTurn()
                checkWinOrDraw()
            }
        }
    }

    private fun getSymbolByPos(pos: Int): FieldSymbol {
        return _fields.value?.get(pos) ?: NONE
    }

    private fun setNoneFields() {
        _fields.value = Array(9) { NONE }
    }

    fun resetGame() {
        setRandomTurn()
        setNoneFields()
        _connectedFieldPos.value = emptyList()
        _isFinished.value = false
    }

    private fun checkWinOrDraw() {
        checkPattern.forEach {
            val meCount = ArrayList<Int>()
            val cpuCount = ArrayList<Int>()
            it.forEach { pos ->
                when (getSymbolByPos(pos)) {
                    NONE -> {}
                    ME -> meCount.add(pos)
                    CPU -> cpuCount.add(pos)
                }
            }
            if (meCount.size >= 3) {
                _connectedFieldPos.value = meCount
                _winnerSymbol.value = ME
                _isFinished.value = true
                return
            } else if (cpuCount.size >= 3) {
                _connectedFieldPos.value = cpuCount
                _winnerSymbol.value = CPU
                _isFinished.value = true
                return
            }
        }
        if (_fields.value?.filter { it == NONE }?.size == 0) {
            _winnerSymbol.value = NONE
            _isFinished.value = true
            return
        }
    }

    private fun toggleTurn() {
        _turn.value = when (_turn.value) {
            ME -> CPU
            CPU -> ME
            else -> throw Exception("Invalid Turn")
        }
    }

    init {
        initializeGame()
    }
}