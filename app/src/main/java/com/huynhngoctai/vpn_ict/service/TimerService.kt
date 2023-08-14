package com.huynhngoctai.vpn_ict.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.huynhngoctai.vpn_ict.App
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.view.act.MainActivity
import com.huynhngoctai.vpn_ict.view.frgment.MainFragment
import com.wireguard.android.backend.Tunnel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

@Suppress("DEPRECATION")
class TimerService : Service() {
    companion object {
        // Channel ID for notifications
        const val CHANNEL_ID = "Stopwatch_Notifications"

        // Service Actions
        const val START = "START"
        const val STOP = "STOP"
        const val GET_STATUS = "GET_STATUS"
        const val MOVE_TO_FOREGROUND = "MOVE_TO_FOREGROUND"
        const val MOVE_TO_BACKGROUND = "MOVE_TO_BACKGROUND"

        // Intent Extras
        const val STOPWATCH_ACTION = "STOPWATCH_ACTION"
        const val TIME_ELAPSED = "TIME_ELAPSED"
        const val IS_STOPWATCH_RUNNING = "IS_STOPWATCH_RUNNING"

        // Intent Actions
        const val STOPWATCH_TICK = "STOPWATCH_TICK"
        const val STOPWATCH_STATUS = "STOPWATCH_STATUS"
    }

    private var timeElapsed: Int = 30
    private var isStopWatchRunning = false

    private var updateTimer = Timer()
    private var stopwatchTimer = Timer()


    // Getting access to the NotificationManager
    private lateinit var notificationManager: NotificationManager

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("Stopwatch", "Stopwatch onBind")
        return null
    }

    /*
    * onStartCommand() is called every time a client starts the service
    * using startService(Intent intent)
    * We will check for what action has this service been called for and then perform the
    * action accordingly. The action is extracted from the intent that is used to start
    * this service.
    * */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        getNotificationManager()

        when (intent?.getStringExtra(STOPWATCH_ACTION)!!) {
            START -> startStopwatch()
            STOP -> stopStopwatch()
            GET_STATUS -> sendStatus()
            MOVE_TO_FOREGROUND -> moveToForeground()
            MOVE_TO_BACKGROUND -> moveToBackground()
        }

        return START_STICKY
    }

    /*
    * This function is triggered when the app is not visible to the user anymore
    * It check if the stopwatch is running, if it is then it starts a foreground service
    * with the notification.
    * We run another timer to update the notification every second.
    * */
    private fun moveToForeground() {

        if (isStopWatchRunning) {
            startForeground(1, buildNotification())

            updateTimer = Timer()

            updateTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    updateNotification()
                }
            }, 0, 1000)
        }
    }

    /*
    * This function is triggered when the app is visible again to the user
    * It cancels the timer which was updating the notification every second
    * It also stops the foreground service and removes the notification
    * */
    private fun moveToBackground() {
        updateTimer.cancel()
        stopForeground(true)
    }

    /*
    * This function starts the stopwatch
    * Sets the status of stopwatch running to true
    * We start a Timer and increase the timeElapsed by 1 every second and broadcast the value
    * with the action of STOPWATCH_TICK.
    * We will receive this broadcast in the MainFragment to get access to the time elapsed.
    * */
    private fun startStopwatch() {
        isStopWatchRunning = true
        timeElapsed = 30 // Reset the timeElapsed to the initial value

        sendStatus()

        stopwatchTimer = Timer()
        stopwatchTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val stopwatchIntent = Intent()
                stopwatchIntent.action = STOPWATCH_TICK

                timeElapsed--

                if (timeElapsed <= 0) {
                    stopStopwatch()
                }

                stopwatchIntent.putExtra(TIME_ELAPSED, timeElapsed)
                sendBroadcast(stopwatchIntent)
            }
        }, 0, 1000)
    }

    /*
    * This function stop the stopwatch
    * */
    private fun stopStopwatch() {
        isStopWatchRunning = false
        timeElapsed = 0
        sendStatus()
        stopwatchTimer.cancel()
    }

    private fun stopVPN() {
        Log.d("stopVPN: ", "" + (App.get().getMyTunnel() != null))
        CoroutineScope(Dispatchers.IO).launch {
            App.getBackend()
                .setState(App.get().getMyTunnel()!!, Tunnel.State.DOWN, App.get().getMyConfig())

            Log.d("stopVPN: ", "" + (App.get().getMyTunnel() != null))
        }
    }

    /*
    * This function is responsible for broadcasting the status of the stopwatch
    * Broadcasts if the stopwatch is running and also the time elapsed
    * */
    private fun sendStatus() {
        val statusIntent = Intent()
        statusIntent.action = STOPWATCH_STATUS
        statusIntent.putExtra(IS_STOPWATCH_RUNNING, isStopWatchRunning)
        statusIntent.putExtra(TIME_ELAPSED, timeElapsed)
        sendBroadcast(statusIntent)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Stopwatch",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(true)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    /*
    * This function is responsible for building and returning a Notification with the current
    * state of the stopwatch along with the timeElapsed
    * */
    @SuppressLint("LaunchActivityFromNotification")
    private fun buildNotification(): Notification {
        val title = if (isStopWatchRunning) {
            "Time of connection VPN Master:"
        } else {
            "VPN Master is stop!"
        }

        val icAction = if (timeElapsed <= 0) R.drawable.start else R.drawable.stop
        val nameAction = if (timeElapsed <= 0) "Start" else "Stop"

        val hours: Int = timeElapsed.div(60).div(60)
        val minutes: Int = timeElapsed.div(60)
        val seconds: Int = timeElapsed.rem(60)

        val cancelIntent = Intent(this, TimerService::class.java)
        cancelIntent.putExtra(STOPWATCH_ACTION, STOP)
        val cancelPendingIntent = PendingIntent.getService(
            this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val intent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setOngoing(true)
            .setContentText(
                "${"%02d".format(hours)}:${"%02d".format(minutes)}:${
                    "%02d".format(
                        seconds
                    )
                }"
            )
            .setColorized(true)
            .setColor(resources.getColor(R.color.gray_bg))
            .setSmallIcon(R.mipmap.icon_launcher)
            .setOnlyAlertOnce(true)
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .addAction(icAction, nameAction, cancelPendingIntent)
            .build()
    }

    /*
    * This function uses the notificationManager to update the existing notification with the new notification
    * */
    private fun updateNotification() {
        notificationManager.notify(
            1,
            buildNotification()
        )
    }
}
