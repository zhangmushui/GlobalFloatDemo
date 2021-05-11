package site.exciter.globalfloat.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import site.exciter.globalfloat.utils.SystemUtils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class MagnetFloatView : FrameLayout {

    companion object {
        const val MARGIN_EDGE = 13
        const val TOUCH_TIME_THRESHOLD = 150
    }

    private var mOriginalRawX: Float = 0.0f
    private var mOriginalRawY: Float = 0.0f
    private var mOriginX: Float = 0.0f
    private var mOriginY: Float = 0.0f
    private var mMagnetFloatListener: MagnetFloatListener? = null
    private var mLastTouchDownTime: Long = 0L
    private var mMoveAnimator: MoveAnimator? = null
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mStatusBarHeight = 0
    private var isNearestLeft = true
    private var isAutoHoverEdge = true
    private var mPortraitY = 0.0f
    private var mTouchDownX = 0.0f

    /**
     * 是否自动悬停到边缘
     */
    fun setAutoHoverEdge(autoHoverEdge: Boolean) {
        this.isAutoHoverEdge = autoHoverEdge
    }

    fun setMagnetFloatListener(listener: MagnetFloatListener) {
        this.mMagnetFloatListener = listener
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mMoveAnimator = MoveAnimator()
        mStatusBarHeight = SystemUtils.getStatusBarHeight(context)
        isClickable = true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (null == event) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> updateViewPosition(event)
            MotionEvent.ACTION_UP -> {
                clearPortraitY()
                moveToEdge()
                if (isOnClickEvent()) {
                    dealClickEvent()
                }
            }
        }
        return true
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        var intercepted = false
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                intercepted = false
                mTouchDownX = event.x
                initTouchDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                intercepted =
                    abs(mTouchDownX - event.x) >= ViewConfiguration.get(context).scaledTouchSlop
            }
            MotionEvent.ACTION_UP -> {
                intercepted = false
            }
        }
        return intercepted
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (null != parent) {
            val isLandScape = newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE
            markPortraitY(isLandScape)
            (parent as ViewGroup).post {
                updateSize()
                moveToEdge(isNearestLeft, isLandScape)
            }
        }
    }

    private fun markPortraitY(isLandScape: Boolean) {
        if (isLandScape) {
            mPortraitY = y
        }
    }

    private fun initTouchDown(event: MotionEvent?) {
        changeOriginalTouchParams(event)
        updateSize()
        mMoveAnimator?.stop()
    }

    private fun changeOriginalTouchParams(event: MotionEvent?) {
        mOriginX = x
        mOriginY = y
        mOriginalRawX = event?.rawX!!
        mOriginalRawY = event.rawY
        mLastTouchDownTime = System.currentTimeMillis()
    }

    private fun updateSize() {
        val viewGroup: ViewGroup = parent as ViewGroup
        mScreenWidth = viewGroup.width - width
        mScreenHeight = viewGroup.height
    }

    private fun isOnClickEvent(): Boolean {
        return System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD
    }

    private fun dealClickEvent() {
        mMagnetFloatListener?.onClick(this)
    }

    private fun updateViewPosition(event: MotionEvent) {
        x = mOriginX + event.rawX - mOriginalRawX
        //限制不可超出屏幕
        var desY = mOriginY + event.rawY - mOriginalRawY
        if (desY < mStatusBarHeight) {
            desY = mStatusBarHeight.toFloat()
        }
        if (desY > mScreenHeight - height) {
            desY = (mScreenHeight - height).toFloat()
        }
        y = desY
    }

    private fun moveToEdge() {
        moveToEdge(isNearestLeft(), false)
    }

    private fun moveToEdge(isLeft: Boolean, isLandScape: Boolean) {
        if (!isAutoHoverEdge) {
            return
        }
        val moveDistance = if (isLeft) MARGIN_EDGE else mScreenWidth - MARGIN_EDGE
        var v = y
        if (!isLandScape && mPortraitY != 0.0f) {
            v = mPortraitY
            clearPortraitY()
        }
        mMoveAnimator?.start(
            moveDistance.toFloat(),
            min(max(0f, v), (mScreenHeight - height).toFloat())
        )
    }

    private fun isNearestLeft(): Boolean {
        val middle = mScreenWidth / 2
        isNearestLeft = x < middle
        return isNearestLeft
    }

    private fun clearPortraitY() {
        mPortraitY = 0.0f
    }

    inner class MoveAnimator : Runnable {
        private var handler: Handler = Handler(Looper.getMainLooper())
        private var destinationX = 0.0f
        private var destinationY = 0.0f
        private var startingTime = 0L

        fun start(x: Float, y: Float) {
            this.destinationX = x
            this.destinationY = y
            startingTime = System.currentTimeMillis()
            handler.post(this)
        }

        fun stop() {
            handler.removeCallbacks(this)
        }

        override fun run() {
            if (null == rootView || null == rootView.parent) {
                return
            }
            val progress = min(1f, (System.currentTimeMillis() - startingTime) / 400f)
            val deltaX = (destinationX - x) * progress
            val deltaY = (destinationY - y) * progress
            move(deltaX, deltaY)
            if (progress < 1) {
                handler.post(this)
            }
        }
    }

    private fun move(deltaX: Float, deltaY: Float) {
        x += deltaX
        y += deltaY
    }
}