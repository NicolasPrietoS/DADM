package co.edu.unal.androidtictactoev2
import kotlin.math.*
class Computer(val difficulty: Int) {
    private val player = "X"
    private val computer = "O"
    private fun isMovesLeft(board:Array<Array<String>>):Boolean{
        for (i in 0..2){
            for (j in 0..2){
                if (board[i][j]==" "){
                    return true
                }
            }
        }
        return false
    }
    private fun evaluate(estado: Array<Array<String>>):Int {
        for (row in 0..2){
            if (estado[row][0] == estado[row][1] && estado[row][1] == estado[row][2]){
                if (estado[row][0] == player){
                    return -10
                } else if (estado[row][0] == computer){
                    return +10
                }
            }

        }
        for (col in 0..2){
            if (estado[0][col] == estado[1][col] && estado[1][col] == estado[2][col]){
                if (estado[0][col] == player){
                    return -10
                } else if (estado[0][col] == computer){
                    return +10
                }
            }
        }
        if (estado[0][0] == estado[1][1] && estado[1][1] == estado[2][2]){
            if (estado[0][0] == player) return -10
            else if (estado[0][0] == computer) return +10
        }
        if (estado[0][2] == estado[1][1] && estado[1][1] == estado[2][0]){
            if (estado[0][2] == player) return -10
            else if (estado[0][2] == computer) return +10
        }
        return 0
    }
    private fun minmax(board: Array<Array<String>>, depth: Int, isMax: Boolean):Int{
        var score = evaluate(board)
        if (score == 10) return score
        if (score == -10) return score
        if (!isMovesLeft(board)) return 0
        if (isMax){
            var best = -1000

            for (i in 0..2){
                for (j in 0..2){
                    if (board[i][j]==" "){
                        board[i][j]=computer
                        best = max(best,minmax(board,depth+1,!isMax))
                        board[i][j] = " "
                    }
                }
            }
            return best
        } else {
            var best = 1000
            for (i in 0..2){
                for (j in 0..2){
                    if (board[i][j]==" "){
                        board[i][j]=player
                        best = min(best,minmax(board,depth+1,!isMax))
                        board[i][j] = " "
                    }
                }
            }
            return best
        }
    }
    fun findBestMove(board: Array<Array<String>>):Array<Int>{
        var bestVal = -1000
        var bestMove = Array<Int>(2){-1}
        for (i in 0..2){
            for (j in 0..2){
                if (board[i][j]==" "){
                    board[i][j]=computer
                    var moveVal = minmax(board,0,false)
                    board[i][j]=" "
                    if (moveVal > bestVal){
                        bestMove = arrayOf(i,j)
                        bestVal = moveVal
                    }
                }
            }
        }
        var probability = 0.0
        if (difficulty==1) probability = 0.5
        else if (difficulty==2) probability = 0.75
        else if (difficulty==3) probability = 0.9
        else probability = 1.0
        val number = (0..100).random()
        if(number.toFloat()/100.0 > probability){
            bestMove = random_move(board)
        }
        return bestMove
    }

    private fun random_move(board: Array<Array<String>>):Array<Int>{
        var rand = (0..8).random()
        while (board[rand/3][rand%3]!=" "){
            rand = (0..8).random()
        }
        return arrayOf(rand/3,rand%3)
    }
}