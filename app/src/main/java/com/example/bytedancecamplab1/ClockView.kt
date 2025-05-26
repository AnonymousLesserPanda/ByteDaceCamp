package com.example.bytedancecamplab1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback, Runnable {

    private var isRunning = false
    private var thread: Thread? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val calendar = Calendar.getInstance()

    init {
        holder.addCallback(this)
    }

    private fun setupBackgroundPaint() {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.color = Color.BLACK
    }

    private fun setupHandPaint(handWidth: Float, color: Int) {
        paint.style = Paint.Style.FILL
        paint.strokeWidth = handWidth
        paint.color = color
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isRunning = true
        thread = Thread(this).apply { start() }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isRunning = false
        thread?.join()
    }

    override fun run() {
        try {
            while (isRunning) {
                draw()
                Thread.sleep(100) // 每0.1秒刷新
            }
        } catch (e: Exception) {
            Log.e("ClockView", "Draw error: ${e.message}")
        }
    }

    private fun draw() {
        val canvas = holder.lockCanvas() ?: return
        try {
            canvas.drawColor(Color.WHITE)
            drawBackground(canvas)
            drawHands(canvas)
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = centerX.coerceAtMost(centerY) - 10f

        // 表盘圆
        setupBackgroundPaint()
        canvas.drawCircle(centerX, centerY, radius, paint)

        // 刻度线
        paint.strokeWidth = 3f
        for (i in 0..360 step 6) {
            val angle = Math.toRadians(i.toDouble()).toFloat()
            val startX = centerX + (radius - 20) * sin(angle)
            val startY = centerY - (radius - 20) * cos(angle)
            val stopX = centerX + radius * sin(angle)
            val stopY = centerY - radius * cos(angle)
            canvas.drawLine(startX, startY, stopX, stopY, paint)
        }
    }

    private fun drawHands(canvas: Canvas) {
        calendar.timeInMillis = System.currentTimeMillis()

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = centerX.coerceAtMost(centerY) - 20

        // 计算角度（时针含分钟偏移）
        val hour = calendar.get(Calendar.HOUR) % 12
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        val hourAngle = (hour + minute / 60f) * 30f
        val minuteAngle = (minute + second / 60f) * 6f
        val secondAngle = second * 6f

        // 绘制指针
        drawHand(canvas, hourAngle, radius * 0.5f, 10f, Color.BLUE)
        drawHand(canvas, minuteAngle, radius * 0.7f, 6f, Color.GREEN)
        drawHand(canvas, secondAngle, radius * 0.9f, 2f, Color.RED)
    }

    private fun drawHand(
        canvas: Canvas,
        angle: Float,
        length: Float,
        handWidth: Float,
        color: Int
    ) {
        val centerX = width / 2f
        val centerY = height / 2f

        setupHandPaint(handWidth, color)

        val startX = centerX + (length - 10) * sin(Math.toRadians(angle.toDouble())).toFloat()
        val startY = centerY - (length - 10) * cos(Math.toRadians(angle.toDouble())).toFloat()
        val stopX = centerX + length * sin(Math.toRadians(angle.toDouble())).toFloat()
        val stopY = centerY - length * cos(Math.toRadians(angle.toDouble())).toFloat()

        canvas.drawLine(startX, startY, stopX, stopY, paint)
    }
}