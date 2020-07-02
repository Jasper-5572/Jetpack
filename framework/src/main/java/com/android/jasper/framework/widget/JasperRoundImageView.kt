package com.android.jasper.framework.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.NinePatchDrawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatImageView
import com.android.jasper.framework.util.ConvertUtils


/**
 *@author   Jasper
 *@create   2020/6/24 11:10
 *@describe
 *@update
 */
class JasperRoundImageView constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    /**
     * 图片的类型，圆形or圆角
     */
    @TypeMode
    var imageType = NORMAL
        set(@TypeMode value) {
            if (this.imageType != value) {
                field = value
                requestLayout()
//                if (value != ROUND && this.type != TYPE_CIRCLE && this.type != TYPE_OVAL) {
//                    this.type = TYPE_OVAL;
//                }
//                requestLayout()
            }
        }

    /**
     * 四个边角的大小
     */
    private var topLeftRadius = 0f
    private var topRightRadius = 0f
    private var bottomLeftRadius = 0f
    private var bottomRightRadius = 0f

    /**
     * 圆角的大小
     */
    private val mCornerRadius = 0f

    /**
     * 圆角图片区域
     */
    private var mRoundRect: RectF = RectF()

    /**
     * 描边的画笔
     */
    private val mBorderPaint by lazy {
        Paint().apply {
            //无锯齿
            isAntiAlias = true
            //加粗
            style = Paint.Style.STROKE
            color = mBorderColor
            strokeWidth = mBorderWidth
        }
    }

    /**
     * 描边的宽度
     */
    var mBorderWidth = 0f
        set(value) {
            if (this.mBorderWidth != value) {
                field = value
                mBorderPaint.strokeWidth = value
            }
        }

    /**
     * 描边的画笔的颜色
     */
    @ColorInt
    var mBorderColor: Int = Color.TRANSPARENT
        set(@ColorInt value) {
            if (this.mBorderColor != value) {
                field = value
                mBorderPaint.color = value
            }
        }

    private val mRoundPath by lazy { Path() }

    /**
     * 图片画笔
     */
    private val mBitmapPaint by lazy {
        Paint().apply {
            //无锯齿
            isAntiAlias = true
        }
    }

    /**
     * 3x3 矩阵，主要用于缩小放大
     */
    private val mScaleMatrix = Matrix()

    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private var mBitmapShader: BitmapShader? = null
    /**
     * imageView的宽度
     */
    private var mWidth = 0

    /**
     * 圆角的半径
     */
    private var mRadius = 0f


    init {
        initAttributes(attrs)
    }

    companion object {
        @IntDef(NORMAL, CIRCLE, ROUND, OVAL)
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        annotation class TypeMode


        const val NORMAL = 0

        /**
         * 圆形图片
         */
        const val CIRCLE = 1
        const val ROUND = 2

        /**
         * 椭圆
         */
        const val OVAL = 3

    }

    /**
     * 初始化自定义属性
     * @param attrs AttributeSet?
     */
    private fun initAttributes(attrs: AttributeSet?) {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawable?.let {
            //.9图片不处理
            if (it.javaClass == NinePatchDrawable::javaClass) {
                return
            }
            //初始化图片
            initBitmapShader()
            canvas?.apply {
                when (imageType) {
                    ROUND -> {
                        initRoundPath()
                        drawPath(mRoundPath, mBitmapPaint)
                        //绘制描边
                        drawPath(mRoundPath, mBorderPaint)
                    }
                    CIRCLE -> {
                        drawCircle(
                            mRadius + mBorderWidth / 2,
                            mRadius + mBorderWidth / 2,
                            mRadius,
                            mBitmapPaint
                        )
                        //绘制描边
                        drawCircle(
                            mRadius + mBorderWidth / 2,
                            mRadius + mBorderWidth / 2,
                            mRadius,
                            mBorderPaint
                        )
                    }
                    OVAL -> {
                        drawOval(mRoundRect, mBitmapPaint)
                        drawOval(mRoundRect, mBorderPaint)
                    }
                    else -> {

                    }
                }

            }


        }
        this.measure(0, 0)
    }

    /**
     * 初始化圆角范围
     * 如果四个圆角大小都是默认值0， 则将四个圆角大小设置为mCornerRadius的值
     */
    private fun initRoundPath() {
        mRoundPath.reset()
        var useCornerRadius = topLeftRadius == 0f
        useCornerRadius = useCornerRadius && topRightRadius == 0f
        useCornerRadius = useCornerRadius && bottomRightRadius == 0f
        useCornerRadius = useCornerRadius && bottomLeftRadius == 0f
        val radii = if (useCornerRadius) floatArrayOf(
            mCornerRadius, mCornerRadius,
            mCornerRadius, mCornerRadius,
            mCornerRadius, mCornerRadius,
            mCornerRadius, mCornerRadius
        ) else floatArrayOf(
            topLeftRadius, topLeftRadius,
            topRightRadius, topRightRadius,
            bottomRightRadius, bottomRightRadius,
            bottomLeftRadius, bottomLeftRadius
        )
        mRoundPath.addRoundRect(mRoundRect, radii, Path.Direction.CW)
    }

    /**
     * 初始化BitmapShader
     */
    private fun initBitmapShader() {
        ConvertUtils.drawableToBitamp(drawable)?.let {bitmap->
            // 将bitmap作为着色器，就是在指定区域内绘制bitmap
            mBitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            var scale = 1.0f
            if (imageType == CIRCLE) {
                // 拿到bitmap宽或高的小值
                val bitmapSize = bitmap.width.coerceAtMost(bitmap.height)
                scale = mWidth * 1.0f / bitmapSize
                //使缩放后的图片居中
                val dx = (bitmap.width * scale - mWidth) / 2
                val dy = (bitmap.height * scale - mWidth) / 2
                mScaleMatrix.setTranslate(-dx, -dy)
            } else if (imageType == ROUND || imageType == OVAL) {
                if (!(bitmap.width == width && bitmap.height == height)) {
                    // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
                    scale = (width * 1.0f / bitmap.width).coerceAtLeast(height * 1.0f / bitmap.height)
                    //使缩放后的图片居中
                    val dx = (scale * bitmap.width - width) / 2
                    val dy = (scale * bitmap.height - height) / 2
                    mScaleMatrix.setTranslate(-dx, -dy)
                }
            }
            // shader的变换矩阵，我们这里主要用于放大或者缩小
            mScaleMatrix.preScale(scale, scale)
            mBitmapShader?.setLocalMatrix(mScaleMatrix)
            // 设置变换矩阵
            mBitmapShader?.setLocalMatrix(mScaleMatrix)
            // 设置shader
            mBitmapPaint.shader = mBitmapShader
        }

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (imageType == CIRCLE) {
            //如果类型是圆形，则强制改变view的宽高一致，以小值为准
            mWidth = MeasureSpec.getSize(widthMeasureSpec)
                .coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
            //图片半径为（宽度-描边的宽度）/2
            mRadius = mWidth / 2 - mBorderWidth / 2
            setMeasuredDimension(mWidth, mWidth)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 圆角图片的范围
        if (imageType == ROUND || imageType == OVAL) {
            mRoundRect = RectF(
                mBorderWidth / 2,
                mBorderWidth / 2,
                w - mBorderWidth / 2,
                h - mBorderWidth / 2
            )
        }
    }

}