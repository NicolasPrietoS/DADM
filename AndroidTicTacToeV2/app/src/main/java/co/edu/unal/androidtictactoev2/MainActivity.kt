package co.edu.unal.androidtic_tac_toe

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private lateinit var buttons: Array<Button>
    private lateinit var statusText: TextView
    private lateinit var btnReset: Button
    private var playerVictory = false
    private var computerVictory = false
    private var playerStart = true
    private val computer = Computer(2)
    private var gameBoard = Array(3){Array(3){" "} }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnReset = findViewById(R.id.reset)
        buttons = arrayOf(
            findViewById(R.id.button_0),
            findViewById(R.id.button_1),
            findViewById(R.id.button_2),
            findViewById(R.id.button_3),
            findViewById(R.id.button_4),
            findViewById(R.id.button_5),
            findViewById(R.id.button_6),
            findViewById(R.id.button_7),
            findViewById(R.id.button_8)
        )
        statusText = findViewById(R.id.information)
        for (i in buttons.indices){
            buttons[i].setOnClickListener {playerMove(i)}
        }
        btnReset.setOnClickListener{reset()}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.new_game -> {
                reset()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun playerMove(index: Int){
        val row = index/3
        val col = index%3

        if (gameBoard[row][col]==" "){
            gameBoard[row][col] = "X"
            buttons[index].text = "X"
            buttons[index].isEnabled = false
            buttons[index].setTextColor(Color.GREEN)
            if (checkWin("X")){
                statusText.text = "Player wins!"
                playerVictory = true
                dissable_buttons()
            }
        }
        if (!playerVictory && !draw()) {
            statusText.text = "Computer's Turn"
            computerMove()
        }

        if (checkWin("O")){
            statusText.text = "Computer Wins!"
            computerVictory = true
            dissable_buttons()
        }
    }
    private fun computerMove() {
        val move = computer.findBestMove(gameBoard)
        gameBoard[move[0]][move[1]] = "O"
        buttons[move[0]*3+move[1]].text = "O"
        buttons[move[0]*3+move[1]].setTextColor(Color.RED)
        buttons[move[0]*3+move[1]].isEnabled = false
    }
    private fun reset(){
        for (i in buttons.indices){
            buttons[i].text = " "
            buttons[i].isEnabled = true
        }
        for (i in gameBoard.indices){
            for (j in gameBoard[i].indices)
                gameBoard[i][j]=" "
        }
        computerVictory = false
        playerVictory = false
        playerStart = !playerStart
        if (!playerStart) {
            if (!playerVictory && !draw()) {
                computerMove()
                statusText.text = "Player's Turn"
            }
        }
    }
    private fun draw():Boolean{
        if (!computerVictory && !playerVictory){
            for (i in 0..2) {
                for (j in 0..2){
                    if (gameBoard[i][j] == " "){
                        return false
                    }
                }
            }
        }
        statusText.text = "Draw"
        return true
    }
    private fun checkWin(player: String): Boolean {
        // Check rows, columns, and diagonals
        for (i in 0..2) {
            if (gameBoard[i].all { it == player }) return true
            if (gameBoard.map { it[i] }.all { it == player }) return true
        }
        if (gameBoard[0][0] == player && gameBoard[1][1] == player && gameBoard[2][2] == player) return true
        if (gameBoard[0][2] == player && gameBoard[1][1] == player && gameBoard[2][0] == player) return true
        return false
    }
    private fun dissable_buttons(){
        for (i in buttons.indices){
            buttons[i].isEnabled = false
        }
    }
}