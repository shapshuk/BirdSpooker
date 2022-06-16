package com.example.birdspooker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.work.*
import com.example.birdspooker.databinding.ActivityMainBinding
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var context : Context
//    private lateinit var alarmManager : AlarmManager

    lateinit var startPicker: NumberPicker
    lateinit var endPicker: NumberPicker
    lateinit var intervalPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        context = this


        startPicker = findViewById(R.id.startPicker)
        startPicker.minValue = 0
        startPicker.maxValue = 23

        endPicker = findViewById(R.id.endPicker)
        endPicker.minValue = 0
        endPicker.maxValue = 23

        intervalPicker = findViewById(R.id.intervalPicker)
        intervalPicker.minValue = 1
        intervalPicker.maxValue = 60



        binding.buttonStart.setOnClickListener() {

            val inputData = Data.Builder()
                .putInt("startTime", startPicker.value)
                .putInt("endTime", endPicker.value)
                .putLong("interval", intervalPicker.value.toLong())
                .build()

            val playerWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<PlayerWorker>()
                .setInputData(inputData)
                .build()

            startWork(playerWorkRequest)
        }

        binding.buttonStop.setOnClickListener() {
            stopWork()

            val toast = Toast.makeText(context, "Timer removed", Toast.LENGTH_LONG)
            toast.show()
        }

    }

    private fun startWork(playerWorkRequest: WorkRequest) {

        stopWork()

        WorkManager.getInstance(context)
            .enqueue(playerWorkRequest)

        val toast = Toast.makeText(context, "Time is set!", Toast.LENGTH_LONG)
        toast.show()

    }

    private fun stopWork() {
        WorkManager.getInstance(context)
            .cancelAllWork()

        Log.d("PlayerWorker", "Timer has been removed")
    }
}
