package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val FLAGS = 0

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var pendingIntent: PendingIntent
    private lateinit var glideImageRadio: RadioButton
    private lateinit var loadAppRadio: RadioButton
    private lateinit var retrofitRadio: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        glideImageRadio = findViewById(R.id.glideImage)
        loadAppRadio = findViewById(R.id.loadApp)
        retrofitRadio = findViewById(R.id.retrofit)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createNotificationChannel()
        custom_button.setOnClickListener {
            val downloadLink = checkSelectedRadio()
            if (downloadLink.isNullOrEmpty()) {
                Toast.makeText(
                    this, "PLease Select one of the choices to download it", Toast.LENGTH_SHORT
                ).show()
            } else {
                custom_button.buttonState = ButtonState.Loading
                download(downloadLink)
            }
        }
    }

    private fun checkSelectedRadio(): String {
        return if (glideImageRadio.isChecked) glideImageLink
        else if (loadAppRadio.isChecked) loadAppLink
        else if (retrofitRadio.isChecked) retrofitLink
        else ""
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download(downloadLink: String) {
        val request =
            DownloadManager.Request(Uri.parse(downloadLink)).setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description)).setRequiresCharging(false)
                .setAllowedOverMetered(true).setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, "/repository.zip"
                )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        var fileName = when (downloadLink) {
            glideImageLink -> "Glide Repo"
            loadAppLink -> "Load App Repo"
            retrofitLink -> "Retrofit Repo"
            else -> ""
        }
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        sendNotification(
            "Your file downloaded successfully, you can access it now.", fileName, "Success"
        )
    }

    companion object {
        const val glideImageLink = "https://github.com/bumptech/glide"
        const val loadAppLink =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        const val retrofitLink = "https://github.com/square/retrofit"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.notificationID),
                getString(R.string.NotificationName),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Your file was downloaded and you can access it now"
            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    @SuppressLint("WrongConstant")
    private fun sendNotification(message: String, fileName: String, status: String) {
        Log.i("TAG", "sendNotification: $status")
        Log.i("TAG", "sendNotification: $fileName")

        val contentIntent = Intent(
            applicationContext, DetailActivity::class.java
        ).putExtra(DetailActivity.EXTRA_DETAIL_STATUS, status)
            .putExtra(DetailActivity.EXTRA_DETAIL_FILENAME, fileName)

        // Create PendingIntent
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext, 1, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(
            applicationContext, applicationContext.getString(R.string.notificationID)
        ).setContentTitle(applicationContext.getString(R.string.notification_title))
            .setSmallIcon(R.drawable.download).setContentText(message)
            .setColor(applicationContext.getColor(R.color.colorPrimary)).addAction(
                R.drawable.download,
                applicationContext.getString(R.string.notification_btn_title),
                contentPendingIntent
            ).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true)
            .setContentIntent(contentPendingIntent)

        val mNotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(mNotificationManager) {
            notify(1, builder.build())
        }
    }
}
