package com.example.birdspooker

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.birdspooker.databinding.ActivityMainBinding
import nl.joery.timerangepicker.TimeRangePicker
import java.time.LocalTime


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var context : Context

    // TODO: - Add to local storage
    private var startTime: TimeRangePicker.Time = TimeRangePicker.Time(9, 0)
    private var endTime: TimeRangePicker.Time = TimeRangePicker.Time(18, 30)

    private var interval: Float = 2.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        context = this

        binding.intervalSlider.addOnChangeListener { _, value, _ -> onIntervalSliderValueChanged(value) }
        binding.timeRangePicker.setOnTimeChangeListener(onTimeChangeListener = onTimeRangePickerListener())
        binding.buttonStart.setOnClickListener() { onStartBtnClicked() }
        binding.buttonStop.setOnClickListener() { onStopBtnClicked() }
    }

    // MARK: - Actions
    private fun onStartBtnClicked() {
        startWork()
        val toast = Toast.makeText(context, "Time is set!", Toast.LENGTH_LONG)
        toast.show()
    }

    private fun onStopBtnClicked() {
        // TODO: - Remove this line
        stopWork()
        val toast = Toast.makeText(context, "Timer removed", Toast.LENGTH_LONG)
        toast.show()
    }

    // TODO: - Set time to TextViews
    private fun onTimeRangePickerListener(): TimeRangePicker.OnTimeChangeListener {
        return object : TimeRangePicker.OnTimeChangeListener {
            override fun onStartTimeChange(startTime: TimeRangePicker.Time) {
                Log.d("TimeRangePicker", "Start time: $startTime")
                this@MainActivity.startTime = startTime

                val startTimeText = if (startTime.minute == 0) {
                    "${startTime.hour}:${startTime.minute}0"
                } else{
                    "${startTime.hour}:${startTime.minute}"
                }
                binding.startTime.text = startTimeText
            }

            override fun onEndTimeChange(endTime: TimeRangePicker.Time) {
                Log.d("TimeRangePicker", "End time: $endTime")
                this@MainActivity.endTime = endTime

                val endTimeText = if (endTime.minute == 0) {
                    "${endTime.hour}:${endTime.minute}0"
                } else{
                    "${endTime.hour}:${endTime.minute}"
                }
                binding.endTime.text = endTimeText
            }

            override fun onDurationChange(duration: TimeRangePicker.TimeDuration) {
                Log.d("TimeRangePicker", "Duration: $duration")
            }
        }
    }

    private fun onIntervalSliderValueChanged(value: Float) {
        Log.d("IntervalSlider", "Interval: $value")
        interval = value
    }


    private fun startWork() {
        val inputData = Data.Builder()
            .putAll(mapOf(
                "startTime" to startTime.toString(),
                "endTime" to endTime.toString(),
                "interval" to interval)
            )
            .build()

        val playerWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<PlayerWorker>()
            .setInputData(inputData)
            .build()

        // TODO: - Remove this line
        stopWork()

        WorkManager.getInstance(context)
            .enqueue(playerWorkRequest)
    }

    private fun stopWork() {
        WorkManager.getInstance(context)
            .cancelAllWork()

        Log.d("PlayerWorker", "Timer has been removed")
    }
}
