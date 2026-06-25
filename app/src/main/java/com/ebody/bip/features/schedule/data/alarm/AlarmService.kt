package com.ebody.bip.features.schedule.data.alarm

import android.app.*
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ebody.bip.R

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private val pendingAlarms = ArrayDeque<Pair<String, String>>()
    private var isPlaying = false

    companion object {
        const val CHANNEL_ID = "medication_alarm_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_NEW_ALARM = "com.ebody.bip.ACTION_NEW_ALARM"
        const val ACTION_DISMISS   = "com.ebody.bip.ACTION_DISMISS_ALARM"
        private const val TAG = "AlarmDebug"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called with action: ${intent?.action}, flags: $flags, startId: $startId")
        when (intent?.action) {

            ACTION_DISMISS -> {
                Log.d(TAG, "Action received: ACTION_DISMISS")
                onCurrentAlarmDismissed()
                return START_NOT_STICKY
            }

            ACTION_NEW_ALARM -> {
                val label  = intent.getStringExtra("ALARM_LABEL") ?: "Medicamento"
                val dosage = intent.getStringExtra("ALARM_DOSAGE") ?: ""
                Log.d(TAG, "Action received: ACTION_NEW_ALARM with label: $label, dosage: $dosage")

                if (!isPlaying) {
                    Log.d(TAG, "Alarm is not currently playing. Starting playback and foreground notification.")
                    isPlaying = true
                    acquireWakeLock()
                    createChannel()
                    // ✅ startForeground apenas UMA vez
                    startForeground(NOTIFICATION_ID, buildNotification(label, dosage))
                    playSound()
                } else {
                    // ✅ Enfileira e apenas ATUALIZA a notificação existente — sem nova entrada
                    Log.d(TAG, "Alarm is already playing. Enqueuing alarm (label: $label, dosage: $dosage) and updating notification.")
                    pendingAlarms.addLast(label to dosage)
                    updateNotification()
                }
            }

            else -> {
                // Primeira chamada sem action (compatibilidade)
                val label  = intent?.getStringExtra("ALARM_LABEL") ?: "Medicamento"
                val dosage = intent?.getStringExtra("ALARM_DOSAGE") ?: ""
                Log.d(TAG, "Fallback/Legacy intent received with label: $label, dosage: $dosage")

                if (!isPlaying) {
                    Log.d(TAG, "Alarm is not currently playing (fallback). Starting playback and foreground notification.")
                    isPlaying = true
                    acquireWakeLock()
                    createChannel()
                    startForeground(NOTIFICATION_ID, buildNotification(label, dosage))
                    playSound()
                } else {
                    Log.d(TAG, "Alarm is already playing (fallback). Enqueuing alarm (label: $label, dosage: $dosage) and updating notification.")
                    pendingAlarms.addLast(label to dosage)
                    updateNotification()
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun buildDismissIntent(): PendingIntent =
        PendingIntent.getService(
            this,
            NOTIFICATION_ID,
            Intent(this, AlarmService::class.java).apply {
                action = ACTION_DISMISS
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        ).also {
            Log.d(TAG, "Built dismiss PendingIntent for service")
        }

    fun onCurrentAlarmDismissed() {
        Log.d(TAG, "onCurrentAlarmDismissed triggered. Pending alarms count: ${pendingAlarms.size}")
        if (pendingAlarms.isEmpty()) {
            // Nenhum alarme pendente — para tudo
            Log.d(TAG, "No more pending alarms. Stopping service via stopSelf().")
            stopSelf()
        } else {
            // Próximo alarme na fila
            val (nextLabel, nextDosage) = pendingAlarms.removeFirst()
            Log.d(TAG, "Next alarm retrieved from queue: label: $nextLabel, dosage: $nextDosage. Restarting sound/notification.")
            stopSound()
            startForeground(NOTIFICATION_ID, buildNotification(nextLabel, nextDosage))
            playSound()
        }
    }

    private fun updateNotification() {
        val total = pendingAlarms.size + 1 // +1 = o que está tocando agora
        val next  = pendingAlarms.lastOrNull()
        Log.d(TAG, "Updating notification. Total pending: $total, Next alarm label: ${next?.first}")

        val contentTitle = "💊 Hora de tomar seus medicamentos"
        val contentText = next?.let { "Próximo: ${it.first} — ${it.second} (+ $total na fila)" }
            ?: "Verifique seus medicamentos pendentes"

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(0, "Dispensar", buildDismissIntent())
            .build()

        // ✅ notify() com o MESMO id apenas ATUALIZA — não cria nova entrada
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, notification)
    }

    private fun stopSound() {
        Log.d(TAG, "Stopping sound. Releasing media player if exists.")
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun acquireWakeLock() {
        Log.d(TAG, "Acquiring WakeLock for 10 minutes.")
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "bip:AlarmWakeLock"
        ).apply { acquire(10 * 60 * 1000L) } // máx 10 min
    }

    private fun buildNotification(label: String, dosage: String): Notification {
        Log.d(TAG, "Building full screen notification for label: $label, dosage: $dosage")
        val fullScreenIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            Intent(this, AlarmActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                putExtra("ALARM_LABEL", label)
                putExtra("ALARM_DOSAGE", dosage)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val dismissIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            Intent(this, AlarmDismissReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⏰ Hora da medicação")
            .setContentText("$label — $dosage")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(0, "Dispensar", dismissIntent)
            .build()
    }

    private fun createChannel() {
        Log.d(TAG, "Creating notification channel if SDK >= O.")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarmes de Medicamento",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações de alarme para medicamentos"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
                setSound(null, null) // ← quem toca é o MediaPlayer, não o canal
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

//    private fun playSound() {
//        mediaPlayer = MediaPlayer().apply {
//            setAudioAttributes(
//                AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_ALARM)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .build()
//            )
//            setDataSource(this@AlarmService, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
//            isLooping = true
//            prepare()
//            start()
//        }
//    }

//    private fun playSound() {
//        mediaPlayer = MediaPlayer().apply {
//            setAudioAttributes(
//                AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_ALARM)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .build()
//            )
//            setDataSource(this@AlarmService, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
//            isLooping = true
//            prepare()
//
//            setVolume(0.1f, 0.1f) // 10% do volume
//
//            start()
//        }
//    }

    private fun playSound() {
        try {
            Log.d(TAG, "Initializing playSound execution.")
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(
                AudioManager.STREAM_ALARM,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                0
            )

            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

            Log.d(TAG, "Using alarm URI: $alarmUri")

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .build()
                )
                setDataSource(this@AlarmService, alarmUri)
                isLooping = true
                prepare()
                setVolume(1.0f, 1.0f)
                start()
            }
            Log.d(TAG, "MediaPlayer started successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing alarm sound", e)
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called. Cleaning up state, stopping sound, and releasing WakeLock.")
        isPlaying = false
        pendingAlarms.clear()
        stopSound()
        wakeLock?.release()
        wakeLock = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}