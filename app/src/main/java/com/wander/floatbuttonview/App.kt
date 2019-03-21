package com.wander.floatbuttonview

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * author wander
 * date 2019/3/19
 *
 */
class App : Application(), Application.ActivityLifecycleCallbacks {
    private var activityCount = 0
    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {
        activityCount--
        if (activityCount == 0) {
            appHide = true
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activityCount++
        appHide = false
    }

    companion object {
        lateinit var instance:Application
        var appHide = true
    }
    override fun onCreate() {
        super.onCreate()
        instance = this

        registerActivityLifecycleCallbacks(this)
    }
}