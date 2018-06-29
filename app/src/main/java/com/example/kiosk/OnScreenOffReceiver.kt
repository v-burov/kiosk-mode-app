package com.example.kiosk

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class OnScreenOffReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_SCREEN_OFF == intent.action) {
            val ctx = context as BaseActivity
            wakeUpDevice(ctx)

        }
    }

    @SuppressLint("WakelockTimeout")
    private fun wakeUpDevice(context: BaseActivity) {
        val wakeLock = context.getWakeLock()
        wakeLock?.apply {
            if (isHeld) {
                release() // release old wake lock
            } else {
                acquire()
                release()
            }
        }
    }
}
