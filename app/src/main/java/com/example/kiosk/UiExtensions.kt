package com.example.kiosk

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.view.*
import androidx.core.content.res.ResourcesCompat


/**
 * Move application foreground
 */
fun Context.moveAppToForeground(taskId: Int) {
    val activityManager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.moveTaskToFront(taskId, 0)
}

/**
 * Enable full screen mode without status bar and navigation menu
 */
fun View.enableFullScreenMode() {
    hideNavMenuAndStatusBar()
    setOnSystemUiVisibilityChangeListener { hideNavMenuAndStatusBar() }
}

/**
 * Define if the app is default launcher
 */
fun Context.isAppLauncherDefault(): Boolean {
    val localPackageManager = packageManager
    val intent = Intent("android.intent.action.MAIN")
    intent.addCategory("android.intent.category.HOME")
    val str = localPackageManager.resolveActivity(intent,
            PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName
    return str == packageName
}

/**
 * Drawing empty view on status bar. This prevents users from pull touchdown status bar.
 */
@Suppress("DEPRECATION")
fun Context.disablePullNotificationTouch(color: Int) {
    val manager = applicationContext
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val localLayoutParams = WindowManager.LayoutParams()
    localLayoutParams.apply {
        type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        gravity = Gravity.TOP
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = (25 * resources.displayMetrics.scaledDensity).toInt()
        format = PixelFormat.RGBX_8888
    }
    val view = EmptyStatusBarView(this)
    view.setBackgroundColor(ResourcesCompat.getColor(resources, color, null))
    manager.addView(view, localLayoutParams)
}

/**
 * View hides pull touchdown status bar
 */
private class EmptyStatusBarView(context: Context) : ViewGroup(context) {
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }
}

/**
 * Hide status bar and navigation menu
 */
private fun hideNavMenuAndStatusBar() =
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
