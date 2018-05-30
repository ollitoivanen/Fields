package com.fields.curiumx.fields;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    public static final String trainingChannelID = "trainingChannel";
    public static final String trainingChannelName = "Current Training";
    private NotificationManager nm;



    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannels();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels(){
        NotificationChannel trainingChannel = new NotificationChannel(trainingChannelID,
                trainingChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        trainingChannel.enableLights(true);
        trainingChannel.enableVibration(true);
        trainingChannel.setLightColor(R.color.colorPrimary);
        trainingChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(trainingChannel);

    }

    public NotificationManager getManager(){
        if (nm==null){
            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return nm;
    }

    public NotificationCompat.Builder getChannelNotification(String title, String message){
        Intent resultIntent = new Intent(this, TrainingActivity.class);
        resultIntent.putExtra("fieldName", message);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(), trainingChannelID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.fields_logo_notification)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setColorized(true)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent);
    }
}
