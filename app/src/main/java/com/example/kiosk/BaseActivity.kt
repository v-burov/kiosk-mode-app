package com.example.kiosk

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.WindowManager
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var onScreenOffReceiver: OnScreenOffReceiver

    override fun onStart() {
        super.onStart()
        registerKioskModeScreenOffReceiver()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        if (!this.isAppLauncherDefault()
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(this)) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            window.decorView.enableFullScreenMode()
            this.disablePullNotificationTouch(getOverlayColor())
        }
    }

    override fun onPause() {
        super.onPause()
        this.moveAppToForeground(taskId)
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(onScreenOffReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    /**
     * Block up and down keys
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_POWER) {
            return true
        }
        val blockedKeys = Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP)
        return blockedKeys.contains(event.keyCode) || super.dispatchKeyEvent(event)
    }

    /**
     * Close every kind of system dialogs
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            val closeDialog = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            sendBroadcast(closeDialog)
        }
        window.decorView.enableFullScreenMode()
    }

    override fun onBackPressed() {
        // disable back press navigation
    }

    /**
     * Register screen off receiver
     */
    private fun registerKioskModeScreenOffReceiver() {
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        onScreenOffReceiver = OnScreenOffReceiver()
        registerReceiver(onScreenOffReceiver, filter)
    }

    /**
     * lazy loading: first call, create wakeLock via PowerManager.
     */
    @Suppress("DEPRECATION")
    fun getWakeLock(): PowerManager.WakeLock? {
        if (wakeLock == null) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "wakeup")
        }
        return wakeLock
    }

    private fun getOverlayColor(): Int {
        return R.color.colorPrimaryDark
    }
}
