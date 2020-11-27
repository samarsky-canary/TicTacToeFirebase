package com.example.tictactoe

import android.util.Log
import android.widget.Toast

class TicTac {
    public var array = Array(3) { i -> IntArray(3) { 0 } };

    public var isGameWinned : Boolean = false;
    constructor() {
        isGameWinned = false;
    }

    private fun CheckWinHorizontal(player: Int): Boolean {
        for (i in array.indices) {
            if (array[i][0] == array[i][1] &&
                array[i][0] == array[i][2] &&
                array[i][0] == player)
                return true;
        }
        return false;
    }

    public fun tictacButtonPressed(cellId: Int, firstPlayer: Boolean) : Boolean {
        if (array[cellId / 3][cellId % 3] != 0)
        {
            return false;
        }
        array[cellId / 3][cellId % 3] = if (firstPlayer) 1 else 2;
        CheckWin(if (firstPlayer) 1 else 2);
        return true;

    }
    private fun CheckWinVertical(player: Int): Boolean {
        for (i in array.indices) {
            if (array[0][i] == array[1][i] &&
                array[0][i] == array[2][i] &&
                array[0][i] == player)
                return true;
        }
        return false;
    }

    private fun CheckWinDiagonal(player: Int): Boolean {
        if (array[0][0] == array[1][1] &&
            array[0][0] == array[2][2] &&
            array[0][0] == player ) {
            return true;
        }

        if (array[0][2] == array[1][1] &&
            array[0][2] == array[2][0] &&
            array[0][2] == player ) {
            return true;
        }
        return false;
    }

    private fun CheckWin(player: Int) : Boolean {
        if (CheckWinHorizontal(player)
            || CheckWinVertical(player)
            || CheckWinDiagonal(player) )
        {
            Log.d("win", true.toString())
            isGameWinned = true;
            return true;
        }

        return false;
    }
}



