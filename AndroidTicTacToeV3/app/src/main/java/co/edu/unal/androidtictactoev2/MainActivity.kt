package co.edu.unal.androidtictactoev2

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private lateinit var buttons: Array<Button>
    private lateinit var statusText: TextView
    private lateinit var btnReset: Button
    private var playerVictory = false
    private var computerVictory = false
    private var playerStart = true
    private var computer = Computer(2)
    private var gameBoard = Array(3){Array(3){" "} }
    private lateinit var mBoardView:BoardView
    private lateinit var mHumanMediaPlayer:MediaPlayer
    private lateinit var mComputerMediaPlayer:MediaPlayer
    private var handler:Handler = Handler()
    val positiveButtonClick = {dialog:DialogInterface, which: Int ->
        setDifficulty(1)
    }
    val neutralButtonClick = {dialog:DialogInterface, which: Int ->
        setDifficulty(2)
    }
    val negativeButtonClick = {dialog:DialogInterface, which: Int ->
        setDifficulty(3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBoardView = findViewById(R.id.board)
        mBoardView.setGame(gameBoard)
        btnReset = findViewById(R.id.reset)
        val toolbar: Toolbar? = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        statusText = findViewById(R.id.information)

        btnReset.setOnClickListener{reset()}
        mBoardView.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                var mGameOver = playerVictory || computerVictory || draw()
                val col = (event?.getX()?.div(mBoardView.getBoardCellWidth()))?.toInt()
                val row = (event?.getY()?.div(mBoardView.getBoardCellWidth()))?.toInt()
                if(!mGameOver){
                    if (row != null) {
                        playerMove(row*3+ col!!)
                    }
                }
                return false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.new_game -> {
                reset()
                true
            }
            R.id.ai_difficulty -> {
                val builder = AlertDialog.Builder(this)
                with(builder){
                    setMessage("Select Difficulty")
                    setPositiveButton("Easy", DialogInterface.OnClickListener(function = positiveButtonClick))
                    setNeutralButton("Medium", DialogInterface.OnClickListener(function = neutralButtonClick))
                    setNegativeButton("Hard", DialogInterface.OnClickListener(function = negativeButtonClick))
                    show()
                }
                return true
            }
            R.id.quit -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun setDifficulty(value:Int){
        computer = Computer(value)
    }


    override protected fun onResume() {
        super.onResume()
        mHumanMediaPlayer = MediaPlayer.create(applicationContext,R.raw.man)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext,R.raw.computer)
    }

    override protected fun onPause() {
        super.onPause()
        mHumanMediaPlayer.release()
        mComputerMediaPlayer.release()
    }
    private fun playerMove(index: Int){
        val row = index/3
        val col = index%3
        var validMove = false
        if (gameBoard[row][col]==" "){
            gameBoard[row][col] = "X"
            if (checkWin("X")){
                statusText.text = "Player wins!"
                statusText.setTextColor(Color.parseColor("#008000"))
                playerVictory = true
            }
            validMove = true
            mBoardView.invalidate()
            mHumanMediaPlayer.start()
        }
        if (!playerVictory && !draw() &&validMove) {
            statusText.text = "Computer's Turn"
            statusText.setTextColor(Color.parseColor("#FF0000"))
            handler.postDelayed(Runnable {
                computerMove()
                statusText.setTextColor(Color.parseColor("#008000"))
                statusText.text = "Player's Turn"
                                         },1000)
        }

        if (checkWin("O")){
            statusText.text = "Computer Wins!"
            statusText.setTextColor(Color.parseColor("#FF0000"))
            computerVictory = true
        }

        draw()
    }
    private fun computerMove() {
        val move = computer.findBestMove(gameBoard)
        gameBoard[move[0]][move[1]] = "O"
        mBoardView.invalidate()
        mComputerMediaPlayer.start()
    }
    private fun reset(){

        for (i in gameBoard.indices){
            for (j in gameBoard[i].indices)
                gameBoard[i][j]=" "
        }
        mBoardView.invalidate()
        computerVictory = false
        playerVictory = false
        playerStart = !playerStart
        if (!playerStart) {
            if (!playerVictory && !draw()) {
                computerMove()
                statusText.text = "Player's Turn"
                statusText.setTextColor(Color.parseColor("#008000"))
            }
        }
    }
    private fun draw():Boolean{
        if (computerVictory || playerVictory) {
            return false
        } else {
            for (i in 0..2) {
                for (j in 0..2){
                    if (gameBoard[i][j] == " "){
                        return false
                    }
                }
            }
        }
        statusText.text = "Draw"
        statusText.setTextColor(Color.parseColor("#1E90FF"))
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
}