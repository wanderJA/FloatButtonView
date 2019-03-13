package com.wander.floatbuttonview

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import java.util.HashMap

/**
 * author wander
 * date 2019/3/12
 *
 */
class FloatButtonHelper : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        FloatBackView(activity).attach(floatViewFactory)
        activityMap[activity]?.onResume()
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d("life", "onActivityCreated")
    }

    companion object {
        val activityMap = HashMap<Context, FloatBackView>()
    }

    fun initFloatButton(activity: Activity) {
        if (!activityMap.contains(activity)) {
            FloatBackView(activity).attach(floatViewFactory)
            activity.application.registerActivityLifecycleCallbacks(this)
        }
    }

    var floatViewFactory: FloatViewFactory = object : FloatViewFactory {
        override fun createView(context: Context): View {
            return ImageView(context).apply {
                setImageResource(R.drawable.abc_ic_arrow_drop_right_black_24dp)
                layoutParams = ViewGroup.LayoutParams(300, 300)
            }
        }

        override fun onClick(v: View) {
            val context = v.context
            Toast.makeText(context, "button", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.component = ComponentName("com.qiyi.video.reader", "com.qiyi.video.reader.activity.FlashActivity")
            context.startActivity(context.packageManager.getLaunchIntentForPackage("com.qiyi.video.reader"))
        }
    }

    interface FloatViewFactory :View.OnClickListener{
        fun createView(context: Context): View
    }
}