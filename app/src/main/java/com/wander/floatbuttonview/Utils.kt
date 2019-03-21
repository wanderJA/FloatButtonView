package com.wander.floatbuttonview

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

/**
 * author wander
 * date 2019/3/21
 *
 */
object Utils {
    fun isAppInstalled(packageName: String): Boolean {
        var pi: PackageInfo? = null

        try {
            val pm = App.instance.packageManager
            pi = pm.getPackageInfo(packageName, 0)
        } catch (var4: Exception) {
            pi = null
        }

        return pi != null
    }

    fun getAppIcon(packageName: String): Drawable? {
        return if (isSpace(packageName)) {
            null
        } else {
            try {
                val pm = App.instance.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                if (pi == null) null else pi!!.applicationInfo.loadIcon(pm)
            } catch (var3: PackageManager.NameNotFoundException) {
                null
            }

        }
    }

    fun getAppName(packageName: String): String? {
        return if (isSpace(packageName)) {
            ""
        } else {
            try {
                val pm = App.instance.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                if (pi == null) null else pi!!.applicationInfo.loadLabel(pm).toString()
            } catch (var3: PackageManager.NameNotFoundException) {
                ""
            }

        }
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) {
            return true
        } else {
            var i = 0

            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }

            return true
        }
    }

}