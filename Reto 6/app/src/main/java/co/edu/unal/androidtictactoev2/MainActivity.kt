package co.edu.unal.androidtictactoev2

import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    private lateinit var statusText: TextView
    private lateinit var computerText: TextView
    private lateinit var playerText: TextView
    private lateinit var drawsText: TextView
    private lateinit var btnReset: Button
    private var playerVictory = false
    private var computerVictory = false
    private var playerStart = true
    private var playerVictories = 0
    private var computerVictories = 0
    private var draws = 0
    private var computer = Computer(2)
    private var gameBoard = Array(3){Array(3){" "} }
    private lateinit var mBoardView:BoardView
    private lateinit var mHumanMediaPlayer:MediaPlayer
    private lateinit var mComputerMediaPlayer: MediaPlayer
    private var handler:Handler = Handler()
    private lateinit var mPrefs: SharedPreferences


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
        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.silence)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.silence)
        mBoardView = findViewById(R.id.board)
        mBoardView.setGame(gameBoard)
        btnReset = findViewById(R.id.reset)
        val toolbar: Toolbar? = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        statusText = findViewById(R.id.information)
        computerText = findViewById(R.id.computer)
        playerText = findViewById(R.id.player)
        drawsText = findViewById(R.id.draws)
        mPrefs = getSharedPreferences("tts_prefs", MODE_PRIVATE)

        playerVictories = mPrefs.getInt("mHumanWins",0)
        computerVictories = mPrefs.getInt("mComputerWins",0)
        draws = mPrefs.getInt("draws",0)
        computer = Computer(mPrefs.getInt("difficulty",2))
        computerText.setText("Computer: "+computerVictories.toString())
        playerText.setText("Player: "+playerVictories.toString())
        drawsText.setText("Empates: "+draws.toString())

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
        if (savedInstanceState != null){
            onRestoreInstanceState(savedInstanceState)
        }
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the board state
        val flattenedBoard = savedInstanceState.getString("board") ?: ""
        val restoredBoard = flattenedBoard.split(",").chunked(3) { it.toTypedArray() }
        for (i in restoredBoard.indices) {
            for (j in restoredBoard[i].indices) {
                gameBoard[i][j] = restoredBoard[i][j]
            }
        }

        // Restore game variables
        playerVictory = savedInstanceState.getBoolean("playerVictory")
        computerVictory = savedInstanceState.getBoolean("computerVictory")
        playerStart = savedInstanceState.getBoolean("playerStart")
        Log.d("DEBUG", flattenedBoard)

        playerVictories = savedInstanceState.getInt("mHumanWins")
        computerVictories = savedInstanceState.getInt("mComputerWins")
        draws = savedInstanceState.getInt("mTies")

        computerText.setText("Computer: "+computerVictories.toString())
        playerText.setText("Player: "+playerVictories.toString())
        drawsText.setText("Empates: "+draws.toString())
        // Update the UI
        mBoardView.invalidate()
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
            R.id.reset_score -> {
                playerVictories = 0
                computerVictories = 0
                draws = 0

                computerText.setText("Computer: "+computerVictories.toString())
                playerText.setText("Player: "+playerVictories.toString())
                drawsText.setText("Empates: "+draws.toString())
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
                playerVictories += 1
                playerText.setText("Player: "+playerVictories.toString())
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
                if (!computerVictory) {
                    statusText.text = "Player's Turn"
                }
                                         },1000)
        }

        draw()
    }
    private fun computerMove() {
        val move = computer.findBestMove(gameBoard)
        gameBoard[move[0]][move[1]] = "O"
        mBoardView.invalidate()
        mComputerMediaPlayer.start()
        if (checkWin("O")){
            statusText.text = "Computer Wins!"
            statusText.setTextColor(Color.parseColor("#FF0000"))
            computerVictory = true
            computerVictories += 1
            computerText.setText("Computer: "+computerVictories.toString())
        }
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
        draws += 1
        statusText.setTextColor(Color.parseColor("#1E90FF"))
        drawsText.setText("Empates: "+draws.toString())
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
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val flattenBoard = gameBoard.flatten().joinToString(",")
        Log.d("DEBUG", flattenBoard)
        outState.putString("board", flattenBoard)
        outState.putInt("mHumanWins", playerVictories)
        outState.putInt("mComputerWins", computerVictories)
        outState.putInt("mTies", draws)
        outState.putBoolean("playerVictory", playerVictory)
        outState.putBoolean("computerVictory", computerVictory)
        outState.putBoolean("playerStart", playerStart)
    }

    override fun onStop(){
        super.onStop()
        var ed: SharedPreferences.Editor = mPrefs.edit()
        ed.putInt("mHumanWins",playerVictories)
        ed.putInt("mComputerWins",computerVictories)
        ed.putInt("draws",draws)
        ed.putInt("difficulty",computer.difficulty)
        ed.commit()
    }
}