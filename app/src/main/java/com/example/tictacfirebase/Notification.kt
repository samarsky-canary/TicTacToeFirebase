package com.example.tictacfirebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Notification {

    val NOTIFYTAG = "new request";
    fun Notify(context: Context, title: String, message: String, number: Int){
        var intent = Intent(context, LoginActivity::class.java);

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;


        var channelId = NotificationChannel("Invitations","Requests", NotificationManager.IMPORTANCE_HIGH);
        nm.createNotificationChannel(channelId);
        var builder = NotificationCompat.Builder(context)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle(title)
            .setContentText(message)
            .setNumber(number)
            .setChannelId(channelId.id)
            .setSmallIcon(R.drawable.bell)
            .setContentIntent(PendingIntent.getActivity(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT))
            .setAutoCancel(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFYTAG, 0, builder.build());
        } else {
            nm.notify(NOTIFYTAG.hashCode(), builder.build());
        }
    }
}