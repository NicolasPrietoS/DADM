package co.edu.unal.androidtictactoev2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BoardView(context: Context, attrs:AttributeSet): View(context,attrs) {
    private lateinit var mHumanBitmap:Bitmap
    private lateinit var mComputerBitmap: Bitmap
    private lateinit var mPaint: Paint
    public fun initialize(){
        mHumanBitmap = BitmapFactory.decodeResource(resources,R.drawable.X)
        mComputerBitmap = BitmapFactory.decodeResource(resources,R.drawable.O)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

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

    }
    init {
        initialize()
    }

}