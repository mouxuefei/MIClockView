package com.exmple.miclockview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*

class MIClockView @JvmOverloads
constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //画边缘的线
    private val mPaintOutCircle = Paint(Paint.ANTI_ALIAS_FLAG)
    //画边缘的文字
    private val mPaintOutText = Paint(Paint.ANTI_ALIAS_FLAG)
    //画进度条
    private val mPaintProgressBg = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintProgress = Paint(Paint.ANTI_ALIAS_FLAG)
    //画三角形
    private val mPaintTriangle = Paint(Paint.ANTI_ALIAS_FLAG)
    //画时针
    private val mPaintHour = Paint(Paint.ANTI_ALIAS_FLAG)
    //画分针
    private val mPaintMinute = Paint(Paint.ANTI_ALIAS_FLAG)
    //画中间小圆球
    private val mPaintBall = Paint(Paint.ANTI_ALIAS_FLAG)
    //半透明的白色
    private val color_halfWhite: Int = Color.argb(255 - 180, 255, 255, 255)
    //纯白色
    private val color_white: Int = Color.parseColor("#FFFFFF")
    // 宽高
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    //中心的坐标
    private var mCenterX: Int = 0
    private var mCenterY: Int = 0
    private val paddingOut: Float = dp2px(12f)
    private val innerRadius: Float = dp2px(6f)
    private var mHourDegress: Int = 0
    private var mMinuteDegress: Int = 0
    private var mSecondMillsDegress: Float = 0f
    private var mSecondDegress: Int = 0
    private var currentProgess: Int = 0

    init {
        mPaintOutCircle.color = color_halfWhite
        mPaintOutCircle.strokeWidth = dp2px(1f)
        mPaintOutCircle.style = Paint.Style.STROKE

        mPaintOutText.color = color_halfWhite
        mPaintOutText.strokeWidth = dp2px(1f)
        mPaintOutText.style = Paint.Style.STROKE
        mPaintOutText.textSize = sp2px(10f).toFloat()
        mPaintOutText.textAlign = Paint.Align.CENTER

        mPaintProgressBg.color = color_halfWhite
        mPaintProgressBg.strokeWidth = dp2px(2f)
        mPaintProgressBg.style = Paint.Style.STROKE

        mPaintProgress.color = color_halfWhite
        mPaintProgress.strokeWidth = dp2px(2f)
        mPaintProgress.style = Paint.Style.STROKE

        mPaintTriangle.color = color_white
        mPaintTriangle.style = Paint.Style.FILL

        mPaintHour.color = color_halfWhite
        mPaintHour.style = Paint.Style.FILL

        mPaintMinute.color = color_white
        mPaintMinute.strokeWidth = dp2px(3f)
        mPaintMinute.style = Paint.Style.STROKE
        mPaintMinute.strokeCap = Paint.Cap.ROUND

        mPaintBall.color = Color.parseColor("#836FFF")
        mPaintBall.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        //设置为正方形
        val imageSize = if (width < height) width else height
        setMeasuredDimension(imageSize, imageSize)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        mCenterX = mWidth / 2
        mCenterY = mHeight / 2
    }

    override fun onDraw(canvas: Canvas) {
        //平移到视图中心
        canvas.translate(mCenterX.toFloat(), mCenterY.toFloat())
        drawArcCircle(canvas)
        drawOutText(canvas)
        drawCalibrationLine(canvas)
        drawSecond(canvas)
        drawMinute(canvas)
        drawHour(canvas)
        drawBall(canvas)
    }

    /**
     * 绘制中间的小球
     */
    private fun drawBall(canvas: Canvas) {
        canvas.drawCircle(0f, 0f, innerRadius / 2, mPaintBall)
    }

    /**
     * 绘制分
     */
    private fun drawMinute(canvas: Canvas) {
        canvas.save()
        canvas.rotate(mMinuteDegress.toFloat())
        canvas.drawLine(0f, 0f, 0f, -(width / 3).toFloat(), mPaintMinute)
        canvas.restore()
    }

    /**
     * 绘制时
     */
    private fun drawHour(canvas: Canvas) {
        canvas.save()
        canvas.rotate(mHourDegress.toFloat())
        canvas.drawCircle(0f, 0f, innerRadius, mPaintTriangle)
        val path = Path()
        path.moveTo(-innerRadius / 2, 0f)
        path.lineTo(innerRadius / 2, 0f)
        path.lineTo(innerRadius / 6, -(width / 4).toFloat())
        path.lineTo(-innerRadius / 6, -(width / 4).toFloat())
        path.close()
        canvas.drawPath(path, mPaintHour)
        canvas.restore()
    }

    /**
     * 绘制秒
     */
    private fun drawSecond(canvas: Canvas) {
        //先绘制秒针的三角形
        canvas.save()
        canvas.rotate(mSecondMillsDegress)
        val path = Path()
        path.moveTo(0f, -width * 3f / 8 + dp2px(5f))
        path.lineTo(dp2px(8f), -width * 3f / 8 + dp2px(20f))
        path.lineTo(-dp2px(8f), -width * 3f / 8 + dp2px(20f))
        path.close()
        canvas.drawPath(path, mPaintTriangle)
        canvas.restore()

        //绘制渐变刻度
        val min = Math.min(width, mHeight) / 2
        for (i in 0..90 step 2) {
            //第一个参数设置透明度，实现渐变效果，从255到0
            canvas.save()
            mPaintProgress.setARGB((255 - 2.7 * i).toInt(), 255, 255, 255)

            //这里的先减去90°，是为了旋转到开始角度，因为开始角度是y轴的负方向
            canvas.rotate(((mSecondDegress - 90 - i).toFloat()))
            canvas.drawLine(min.toFloat() * 3 / 4, 0f, min * 3 / 4 + dp2px(10f), 0f, mPaintProgress);
            canvas.restore()
        }
    }

    /**
     * 绘制刻度线
     * desc:每个2°绘制一个刻度，也就是有180个刻度
     */
    private fun drawCalibrationLine(canvas: Canvas) {
        val min = Math.min(width, mHeight) / 2
        for (i in 0 until 360 step 2) {
            canvas.save()
            canvas.rotate(i.toFloat())
            canvas.drawLine(min.toFloat() * 3 / 4, 0f, min * 3 / 4 + dp2px(10f), 0f, mPaintProgressBg);
            canvas.restore()
        }
    }

    /**
     * 然后绘制4个文字刻度：3,6,9,12
     */
    private fun drawOutText(canvas: Canvas) {
        val min = Math.min(width, mHeight)
        val textRadius = (min - paddingOut) / 2
        val fm = mPaintOutText.fontMetrics
        //文字的高度
        val mTxtHeight = Math.ceil((fm.leading - fm.ascent).toDouble()).toInt()
        canvas.drawText("3", textRadius, (mTxtHeight / 2).toFloat(), mPaintOutText)
        canvas.drawText("9", -textRadius, (mTxtHeight / 2).toFloat(), mPaintOutText)
        canvas.drawText("6", 0f, textRadius + mTxtHeight / 2, mPaintOutText)
        canvas.drawText("12", 0f, -textRadius + mTxtHeight / 2, mPaintOutText)
    }

    /**
     * 首先画外边缘的弧度，分成四个弧度，假设每个弧度中间的间隔是6个弧度
     * 那么弧度的位置是 5-85,95-175,185 -265,275-355；
     */
    private fun drawArcCircle(canvas: Canvas) {
        val min = Math.min(width, mHeight)
        val rect = RectF(-(min - paddingOut) / 2, -(min - paddingOut) / 2, (min - paddingOut) / 2, (min - paddingOut) / 2)
        canvas.drawArc(rect, 5f, 80f, false, mPaintOutCircle)
        canvas.drawArc(rect, 95f, 80f, false, mPaintOutCircle)
        canvas.drawArc(rect, 185f, 80f, false, mPaintOutCircle)
        canvas.drawArc(rect, 275f, 80f, false, mPaintOutCircle)

    }

    // 指针转动的方法
    fun startTick() {
        // 一秒钟刷新一次
        postDelayed(mRunnable, 150)
    }

    private val mRunnable = Runnable {
        calculateDegree()
        invalidate()
        startTick()
    }

    private fun calculateDegree() {
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = System.currentTimeMillis()
        val minute = mCalendar.get(Calendar.MINUTE)
        val secondMills = mCalendar.get(Calendar.MILLISECOND)
        val second = mCalendar.get(Calendar.SECOND)
        val hour = mCalendar.get(Calendar.HOUR)
        mHourDegress = hour * 30
        mMinuteDegress = minute * 6
        mSecondMillsDegress = second * 6 + secondMills * 0.006f
        mSecondDegress = second * 6
        val mills = secondMills * 0.006f
        //因为是没2°旋转一个刻度，所以这里要根据毫秒值来进行计算
        when (mills) {
            in 2 until 4 -> {
                mSecondDegress +=2
            }
            in 4 until 6 -> {
                mSecondDegress += 4
            }
        }
    }

    /**
     * 调用时机：onAttachedToWindow是在第一次onDraw前调用的,只调用一次
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startTick()
    }

    /**
     * 调用时机：我们销毁View的时候。我们写的这个View不再显示。
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(mRunnable)
    }

}
/**
 * dp转px
 */
fun View.dp2px(dipValue: Float): Float {
    return (dipValue * this.resources.displayMetrics.density + 0.5f)
}

/**
 * px转dp
 */
fun View.px2dp(pxValue: Float): Float {
    return (pxValue / this.resources.displayMetrics.density + 0.5f)
}

/**
 * sp转px
 */
fun View.sp2px(spValue: Float): Float {
    return (spValue * this.resources.displayMetrics.scaledDensity + 0.5f)
}