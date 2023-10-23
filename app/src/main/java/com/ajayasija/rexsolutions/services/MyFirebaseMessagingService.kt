package com.ajayasija.rexsolutions.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ajayasija.rexsolutions.MainActivity
import com.ajayasija.rexsolutions.R
import com.ajayasija.rexsolutions.data.UserPref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class MyFirebaseMessagingService: FirebaseMessagingService() {

    var data: Map<String, String>? = null
    //lateinit var proceedToViewModel: ProceedToPayViewModel
    private val TAG = MyFirebaseMessagingService::class.java.simpleName
    private val CHANNEL_ID = "OSNAP"
    lateinit var context: Context // create context
    //  var user: User? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            super.onMessageReceived(remoteMessage)
            context = this
            /* proceedToViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                 .create(ProceedToPayViewModel::class.java)*/
            Log.e(
                "remoteMessage",
                "" + remoteMessage.data
            )
            Log.e(TAG, "From: " + remoteMessage.from)
            data = remoteMessage.data
            Log.e("remoteMessage", "" + data)
            Log.e("rating", "" + data?.get("photographer_rating"))

            val typeee = data?.get("type").toString()

            /*   if(AppConstants.isBookingDetailsVisible) {
                 if (typeee.equals(
                         "CancelledByphotographer",
                         true
                     ) || typeee.equals("Cancelled By photographer", true)
                 ) {
                     callMapsDetailsMethod()
                 }
                 else  if (typeee.equals(
                         "CancelledBySystem",
                         true
                     ) || typeee.equals("Cancelled By System", true)
                 ) {
                     callMapsDetailsMethod()
                 }
                 else {
                     callOrderListsMethod()
                 }
             }
             else if(AppConstants.isMyRatingVisible){
                 if (typeee.equals(
                         "CancelledByphotographer",
                         true
                     ) || typeee.equals("Cancelled By photographer", true)
                 ) {
                     callMapsDetailsMethod()
                 }
                 else  if (typeee.equals(
                         "CancelledBySystem",
                         true
                     ) || typeee.equals("Cancelled By System", true)
                 ) {
                     callMapsDetailsMethod()
                 }
             }
             else if(AppConstants.isMyTrackingVisible) {
                 if (typeee.equals(
                         "CancelledByphotographer",
                         true
                     ) || typeee.equals("Cancelled By photographer", true)
                 ) {
                     callMapsDetailsMethod()
                 }
                 else  if (typeee.equals(
                         "CancelledBySystem",
                         true
                     ) || typeee.equals("Cancelled By System", true)
                 ) {
                     callMapsDetailsMethod()
                 }
                 else {
                     callOrderListsMethod()
                 }
             }
             else if(AppConstants.isMyUpcomingVisible){
                 callOrderListsMethod()
             }*/

            val titl = data?.get("title").toString()
            if(titl != null || (titl != "null")) {
                sendNotification(this, data!!["title"].toString(),typeee)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun callMapsDetailsMethod()
    {
        val myIntent = Intent("FBR-CANCEL")
        this.sendBroadcast(myIntent)
    }
    fun callOrderListsMethod()
    {
        val myIntent = Intent("FBR-All")
        this.sendBroadcast(myIntent)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("Token", "token in firebase instance service class: $token")
        UserPref(applicationContext).saveToken(token)
    }

    private fun sendNotification(mCtext: Context, notiData: String, notiType:String) {
        var body = ""
        var lead_IDD = ""
        var notiFicationType = ""
        val r = Random()
        val random = r.nextInt(10000)
        var notificationIntent: Intent?
        val title = notiData
        body = data!!["message"].toString()
        if(notiType == "Gallery link Updated" || notiType == "GallerylinkUpdated"){
            if(body == null || body == "null"){
                body = "Gallery link has been updated for your snapzo request."
            }
        }
        else if(notiType == "Edited gallery images" || notiType == "Editedgalleryimages"){
            if(body == null || body == "null"){
                body = "Edited gallery images has been updated for your snapzo! request."
            }
        }
        val CHANNEL_ID = "1234"
        val soundUri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mCtext.packageName + "/" /*R.raw.happy_bells_notification*/ )
        val mNotificationManager =
            mCtext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //For API 26+ you need to put some additional code like below:
        val mChannel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(
                CHANNEL_ID,
                "snapzo",
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.lightColor = Color.GRAY
            mChannel.enableLights(true)
            //    mChannel.description = Utils.CHANNEL_SIREN_DESCRIPTION
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            mChannel.setSound(soundUri, audioAttributes)
            mNotificationManager.createNotificationChannel(mChannel)
        }
        lead_IDD = data!!["lead_id"].toString()
        notificationIntent = Intent(this, MainActivity::class.java)
        /* notificationIntent.putExtra(AppConstants.KEY_TYPE_LEAD_Booking_ID,lead_IDD)
         notificationIntent.putExtra(AppConstants.KEY_TYPE_Notification_Type,notiType)
         notificationIntent.putExtra(AppConstants.COME_FROM,AppConstants.NOTIFICATION)*/
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        var pendingIntent : PendingIntent? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        //General code:
        val status = NotificationCompat.Builder(
            mCtext, CHANNEL_ID
        )
        status.setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.logo_auto_check) //.setOnlyAlertOnce(true)
            .setContentTitle(title)
            .setContentText(body)
            .setTicker(getString(R.string.app_name))
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setVibrate(longArrayOf(0, 500, 1000))
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setDefaults(Notification.DEFAULT_LIGHTS)
            .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mCtext.packageName + "/" /*R.raw.happy_bells_notification*/))
            .setContentIntent(pendingIntent)
        mNotificationManager.notify(random, status.build())
    }

    /*  private fun BroadcastTaskmessage(message: PaymentStatus.PaymentDetailsData) {
          AppUtil.LogMsg(TAG, AppUtil.getCustomGson()!!.toJson(message))
          if (!WApplication.isBackgroundRunning()) {
              val context: Context = WApplication.getAppInstance()!!.applicationContext
              val intent = Intent(context, ProceedToPay::class.java)
              intent.action = "AcceptRequest"
              val b = Bundle()
              b.putString("updatedData", AppUtil.getCustomGson()!!.toJson(message))
              intent.putExtras(b)
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              context.startActivity(intent)

              //sendBroadcast(intent)
          }
      }*/


    override fun handleIntent(intent: Intent) {
        try {
            if (intent.extras != null) {
                if (UserPref(applicationContext).getUser() != null) {
                    val builder = RemoteMessage.Builder("MessagingService")
                    for (key in intent.extras!!.keySet()) {
                        builder.addData(key!!, intent.extras!![key].toString())
                    }
                    onMessageReceived(builder.build())
                }
            } else {
                super.handleIntent(intent)
            }
        } catch (e: java.lang.Exception) {
            super.handleIntent(intent)
        }
    }
}