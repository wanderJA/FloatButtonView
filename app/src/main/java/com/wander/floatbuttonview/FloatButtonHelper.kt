package com.wander.floatbuttonview

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.float_button_layout.view.*
import java.util.*

/**
 * author wander
 * date 2019/3/12
 *
 */
class FloatButtonHelper : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        val floatBackView = activityMap[activity]
        if (floatBackView == null && FloatBackView.needShow) {
            FloatBackView(activity).attach(floatViewFactory)
        }
        activityMap[activity]?.onResume()
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        activityMap.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d("life", "onActivityCreated")
    }

    companion object {
        val activityMap = WeakHashMap<Context, FloatBackView>()
        const val appName = "com.qiyi.video.reader"
    }

    fun initFloatButton(activity: Activity) {
        if (!activityMap.contains(activity)) {
            FloatBackView(activity).attach(floatViewFactory)
            activity.application.registerActivityLifecycleCallbacks(this)
        }
    }

    var floatViewFactory: FloatViewFactory = object : FloatViewFactory {
        override fun createView(context: Context): View {
            val view = LayoutInflater.from(context).inflate(R.layout.float_button_layout, null)
            view.appName.text = Utils.getAppName(appName)
            val appIcon = Utils.getAppIcon(appName)
            if (appIcon != null) {
                view.appIcon.setImageDrawable(appIcon)
                view.appIcon.visibility = View.VISIBLE
            }

            return view
        }

        override fun onClick(v: View) {
            val context = v.context
            Toast.makeText(context, "button", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.component = ComponentName("com.qiyi.video.reader", "com.qiyi.video.reader.activity.FlashActivity")
            if (Utils.isAppInstalled(appName)) {
                context.startActivity(context.packageManager.getLaunchIntentForPackage(appName))
            } else {
                FloatBackView.needShow = false
                activityMap[v.context]?.onResume()
            }

        }
    }

    interface FloatViewFactory : View.OnClickListener {
        fun createView(context: Context): View
    }
}