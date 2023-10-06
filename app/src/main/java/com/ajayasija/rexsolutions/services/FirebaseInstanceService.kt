package com.ajayasija.rexsolutions.services


import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ajayasija.rexsolutions.MainActivity
import com.ajayasija.rexsolutions.R
import com.ajayasija.rexsolutions.data.UserPref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "channelId"
const val channelName = "channelName"

class FirebaseInstanceService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.e("Token", "token in firebase instance service class: $token")
        UserPref(applicationContext).saveToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            Log.e("notification message", "onMessageReceived: ${message.notification!!.title!!}")
            addNotification(message.notification!!.title!!, message.notification!!.body!!)
        }
    }

    private fun addNotification(title: String, desc: String) {

        val notificationIntent = Intent(
            this,
            MainActivity::class.java
        )
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        // Add action button in the notification
        val intent = Intent("action.name")
        val pIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)


        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_auto_check)
            .setContentTitle(title)
            .setContentText(desc)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //.setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.accept, "Accept", pIntent)


        val action = NotificationCompat.Action(
            R.drawable.accept,
            "Accept",
            pendingIntent
        )
/*
        //create a notification channel or category
        val name = getString(R.string.default_notification_channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(
            getString(R.string.default_notification_channel_id),
            name,
            importance
        )
        mChannel.description = descriptionText*/
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //manager.createNotificationChannel(mChannel)
        manager.notify(0, builder.build())
    }

    private fun remoteView(title: String, desc: String) {
        val customLayout =
            RemoteViews(packageName,  R.layout.custom_notification)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.custom_notification)

        val acceptIntent = Intent("Accept_Action")
        val acceptPendingIntent =
            PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val rejectIntent = Intent("Reject_Action")
        val rejectPendingIntent =
            PendingIntent.getBroadcast(this, 0, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        customLayout.setOnClickPendingIntent(
            R.id.accept_button,
            acceptPendingIntent
        )
        customLayout.setOnClickPendingIntent(
            R.id.reject_button,
            rejectPendingIntent
        )

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(
                this,
                getString(R.string.default_notification_channel_id)
            )
                .setSmallIcon(R.drawable.logo_auto_check)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(customLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setAutoCancel(true)
                .setContent(customLayout)

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(0, builder.build())
    }

}