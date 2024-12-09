package co.edu.unal.androidtictactoev2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class BoardView(context: Context, attrs:AttributeSet): View(context,attrs) {
    private lateinit var mHumanBitmap:Bitmap
    private lateinit var mComputerBitmap: Bitmap
    private lateinit var mPaint: Paint
    public fun initialize(){
        mHumanBitmap = BitmapFactory.decodeResource(resources,R.drawable.player)
        mComputerBitmap = BitmapFactory.decodeResource(resources,R.drawable.computer)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    public fun getBoardCellWidth():Int{
        return width/3
    }

    public fun getBoardCellHeight():Int{
        return height/3
    }

    private lateinit var mGame:Array<Array<String>>

    public fun setGame(game:Array<Array<String>>){
        mGame = game
    }
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var boardWidth = width
        var boardHeight = height

        mPaint.setColor(Color.LTGRAY)
        mPaint.strokeWidth = (10).toFloat()

        val cellWidth = (boardWidth/3).toFloat()
        canvas.drawLine(cellWidth,(0).toFloat(),cellWidth,boardHeight.toFloat(),mPaint)
        canvas.drawLine(cellWidth*2,(0).toFloat(),cellWidth*2,boardHeight.toFloat(),mPaint)

        canvas.drawLine((0).toFloat(),cellWidth,boardHeight.toFloat(),cellWidth,mPaint)
        canvas.drawLine((0).toFloat(),cellWidth*2,boardHeight.toFloat(),cellWidth*2,mPaint)

        for (i in 0..2){
            for (j in 0..2){

                val leftSide = (width/3)*j
                val rightSide = (width/3)*(j+1)
                val topSide = (height/3)*i
                val bottomSide = (height/3)*(i+1)

                if (mGame[i][j]=="X"){
                    canvas.drawBitmap(mHumanBitmap,
                        null,
                        Rect(leftSide,topSide,rightSide,bottomSide),
                        null)
                } else if (mGame[i][j] == "O"){
                    canvas.drawBitmap(mComputerBitmap,
                        null,
                        Rect(leftSide,topSide,rightSide,bottomSide),
                        null)
                }
            }
        }
    }
    init {
        initialize()
    }
}