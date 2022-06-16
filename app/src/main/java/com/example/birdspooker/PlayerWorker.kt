package com.example.birdspooker

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*
import java.util.concurrent.TimeUnit

class PlayerWorker(context : Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters){

    override fun doWork(): Result {

        try {

            val startTime = inputData.getInt("startTime", 0)
            val endTime = inputData.getInt("endTime", 23)
            Log.d("PlayerWorker", "Start time = $startTime, End time = $endTime")

            val c = Calendar.getInstance()
            val timeOfDay = c[Calendar.HOUR_OF_DAY]

            // solution of night problem (?)
            if ((endTime < startTime) && ((timeOfDay<endTime)||(timeOfDay>=startTime))) {
                playMusic()
            } else if (timeOfDay in startTime until endTime) {
                playMusic()
            }

        } catch (ex : Exception) {
            return Result.failure()
        }

        val tenMinuteRequest = OneTimeWorkRequestBuilder<PlayerWorker>()
            .setInitialDelay(inputData.getLong("interval", 15), TimeUnit.MINUTES)
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