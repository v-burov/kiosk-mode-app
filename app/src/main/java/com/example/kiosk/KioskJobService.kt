package com.example.kiosk

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent

class KioskJobService : JobService() {

    override fun onStartJob(p0: JobParameters?): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }
}
