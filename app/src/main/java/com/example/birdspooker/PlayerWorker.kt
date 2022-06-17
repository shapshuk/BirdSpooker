package com.example.birdspooker

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class PlayerWorker(context : Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val startTime = inputData.keyValueMap["startTime"] as? LocalTime
        val endTime = inputData.keyValueMap["startTime"] as? LocalTime
        val interval = inputData.getFloat("interval", 15F)

        Log.d("PlayerWorker", "Start time = $startTime, End time = $endTime")

        val time = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
        if (time.isAfter(startTime) && time.isBefore(endTime)) {
            playMusic()
        }

        // TODO: - Enqueue unique work
        val tenMinuteRequest = OneTimeWorkRequestBuilder<PlayerWorker>()
            .setInitialDelay(interval.toLong(), TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(tenMinuteRequest)

        return Result.success()
    }

    private fun playMusic(){
        val randSound = (1..3).random()
        val localUri : Uri = Uri.parse("android.resource://com.example.birdspooker/raw/sound$randSound")

        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, localUri)
            prepare()
        }

        mediaPlayer.start()
    }
}