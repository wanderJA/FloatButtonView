package com.wander.floatbuttonview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

/**
 * author wander
 * date 2019/3/12
 *
 */
class FloatButtonView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val tag = "FloatButtonView"
    /**
     * 用户意图滑动的距离
     */
    private var hoverSlidDistance = resources.displayMetrics.heightPixels / 20
    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()


    var lock = false
    private var contentView: View? = null
    private var buttonWidth = 0
        set(value) {
            field = value
            slideOutRange = field * slideOutRangePercent
        }
    private var viewHeight = 0
    var slideOutVelocity = 500f
    var slideOutRangePercent = 0.35f
    private var slideOutRange = 100f
    private val mDragHelper = ViewDragHelper.create(this, 1.0f, MyDragCallback())
    var dragStateListener: DragStateListener? = null
    val screeHeight = resources.displayMetrics.heightPixels

    init {

        initOrgPosition(screeHeight)

    }


    fun destroy() {
        FloatButtonHelper.activityMap.remove(context)
    }

    fun resumed() {
        contentView?.top = orgContentY
    }

    fun create(view: View) {
        if (context is Activity) {
            buttonWidth = view.width
            setBackgroundColor(Color.parseColor("#55ff0000"))
            addView(view)
            bringChildToFront(view)
            contentView = view
            val decorView = (context as Activity).window.decorView
            val flp =
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            (decorView.findViewById(android.R.id.content) as FrameLayout).addView(this, flp)
        } else {
            Log.d(tag, "only support activity")
        }
    }


    companion object {
        var orgContentY = 1000
        var hasInit = false
        fun initOrgPosition(screeHeight: Int) {
            if (!hasInit && screeHeight != 0) {
                orgContentY = screeHeight * 4 / 5
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            contentView = getChildAt(0)
            contentView?.top = orgContentY
            contentView?.left = 0
        } else {
            throw IllegalArgumentException("need one direct child")
        }
    }

    fun dismiss() {
        dragStateListener?.onClose()
        contentView?.top = 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(tag, "height:$height")
        viewHeight = height
        buttonWidth = contentView?.width ?: 0
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d(tag, "orgY:${contentView?.top}")
        orgContentY = contentView?.top ?: 0
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDragHelper.processTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate()
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                mDownX = event.x
//                mDownY = event.y
//            }
//            MotionEvent.ACTION_MOVE -> {
//                //当滑动大于hoverSlidDistance时开始相应drag
//                if (Math.abs(event.y - mDownY) < hoverSlidDistance) {
//                    return false
//                }
//                if (Math.abs(event.x - mDownX) > hoverSlidDistance) {
//                    return false
//                }
//            }
//            else -> {
//            }
//        }
//        if (lock) {
//            return false
//        }

        return mDragHelper.shouldInterceptTouchEvent(event)
    }


    inner class MyDragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child == contentView
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            Log.d(tag, "clampViewPositionVertical dy$dy   top:$top")
            orgContentY = top
            return Math.max(Math.min(top, screeHeight - (contentView?.height ?: 0)), 0)
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            Log.d(tag, "clampViewPositionHorizontal dx$dx   left:$left")
            return Math.min(0, left)
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return viewHeight
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return buttonWidth
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if (releasedChild == contentView) {
                if (Math.abs(xvel) > slideOutVelocity) {
                    mDragHelper.settleCapturedViewAt(-buttonWidth, orgContentY)
                    invalidate()
                    close()
                    return
                }

                val left = Math.abs(contentView?.left ?: 0)
                Log.d(tag, "left:$left\tslideOutRange$slideOutRange")
                if (left > slideOutRange) {
                    mDragHelper.settleCapturedViewAt(-buttonWidth, orgContentY)
                    close()
                } else {
                    mDragHelper.settleCapturedViewAt(0, orgContentY)
                }
                invalidate()
            }
        }

        override fun onViewDragStateChanged(state: Int) {
//            when (state) {
//                ViewDragHelper.STATE_IDLE -> {
//                    if (contentView?.left == -buttonWidth) {
//                        close()
//                    }
//
//                }
//                else -> {
//                }
//            }
        }


    }

    private fun close() {
        dragStateListener?.onClose()
        destroy()
        Log.d(tag, "close")
    }


}