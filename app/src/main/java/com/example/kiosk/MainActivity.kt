package com.example.kiosk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast

class MainActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE_HOME = 1
        private const val REQUEST_CODE_PERMISSION_OVERLAY = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkHome()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_HOME -> checkHome(isShowToast = true)
            REQUEST_CODE_PERMISSION_OVERLAY -> checkPermissionOverlay(isShowToast = true)
        }
    }

    private fun checkHome(isShowToast: Boolean = false) {
        if (!this.isAppLauncherDefault()) {
            requestPermission(Settings.ACTION_HOME_SETTINGS, REQUEST_CODE_HOME, R.string.error_set_app_launcher, isShowToast)
        } else {
            checkPermissionOverlay()
        }
    }

    private fun checkPermissionOverlay(isShowToast: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            requestPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, REQUEST_CODE_PERMISSION_OVERLAY, R.string.error_no_permission_overlay, isShowToast)
        }
    }

    private fun requestPermission(permission: String, requestCode: Int, errorMessage: Int, isShowToast: Boolean) {
        if (isShowToast) Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        startActivityForResult(Intent(permission), requestCode)
    }
}
