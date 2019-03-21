package com.wander.floatbuttonview

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout

/**
 * author wander
 * date 2019/3/13
 *
 */
class FloatBackView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val tag = "FloatBackView"
    private var mLastX = 0f
    private var mLastY = 0f
    private var mTouchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop shl 1
    private var hasDrag = false
    private var gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            Log.d(tag, "velocityX:$velocityX")
            if (velocityX < -400) {
                this@FloatBackView.hide()
            }
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            Log.d(tag, "distanceX:$distanceX\tdistanceY:$distanceY")
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    })

    companion object {

        /**
         * 记录位置
         */
        var currentTopMargin = 1000

        var needShow = true
    }

    init {
        setBackgroundColor(Color.parseColor("#55ff0000"))

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
        params.topMargin = currentTopMargin

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Log.d(tag, "dispatchTouchEvent${event.action}")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                hasDrag = false
                mLastX = event.rawX
                mLastY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val curX = event.rawX
                val curY = event.rawY
                if (!hasDrag) {
                    if (Math.pow((curX - mLastX).toDouble(), 2.0) + Math.pow(
                            (curY - mLastY).toDouble(),
                            2.0
                        ) > mTouchSlop
                    ) {
                        hasDrag = true
                        mLastX = curX
                        mLastY = curY
                    }
                } else {
                    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return super.onTouchEvent(event)
                    val distanceX = (curX - mLastX).toInt()
                    params.leftMargin += distanceX
                    if (params.leftMargin > 0) {
                        params.leftMargin = 0
                    }
                    params.topMargin += (curY - mLastY).toInt()
                    requestLayout()
                    mLastX = curX
                    mLastY = curY
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (!hasDrag) {
                    performClick()
                    return true
                }
                moveIfNeeded()
            }
        }
        gestureDetector.onTouchEvent(event)
        return true
    }

    private fun moveIfNeeded() {
        val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
        if (params.leftMargin < 0) {
            if (params.leftMargin < -width / 2) {
                //滑动一半隐藏
                params.leftMargin = -width
                hide()
            }
            params.leftMargin = 0
        }
        if (params.topMargin < dp2px(30)) {
            params.topMargin = dp2px(30)
        }
        if (params.leftMargin + width > (parent as View).width) {
            params.leftMargin = (parent as View).width - width
        }
        if (params.topMargin + height > (parent as View).height) {
            params.topMargin = (parent as View).height - height
        }
        currentTopMargin = params.topMargin
        requestLayout()
    }

    private fun hide() {
        visibility = View.GONE
        needShow =false
        Log.d(tag, "close")

    }

    private fun dp2px(dip: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dip * density + 0.5).toInt()
    }

    fun attach(floatViewFactory: FloatButtonHelper.FloatViewFactory) {
        val decorView = (context as Activity).window.decorView
        val view = floatViewFactory.createView(context)
        addView(view)
        setOnClickListener(floatViewFactory)
        val flp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        val content = decorView.findViewById(android.R.id.content) as FrameLayout
        content.addView(this, flp)
        FloatButtonHelper.activityMap[context] = this
    }

    fun onResume() {
        if (needShow) {
            val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
            params.topMargin = currentTopMargin
            bringToFront()
        } else {
            hide()
            FloatButtonHelper.activityMap.remove(context)
        }
    }


}