package com.fields.curiumx.fields.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.fields.curiumx.fields.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String followerChannelID = "followerChannel";
    public static final String followerChannelName = "New Followers";
    NotificationManager nm;

    @TargetApi(Build.VERSION_CODES.O)
    public void setupChannels(){
        NotificationChannel trainingChannel = new NotificationChannel(followerChannelID,
                followerChannelName, NotificationManager.IMPORTANCE_HIGH);
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

    @Override public void onMessageReceived(RemoteMessage remoteMessage) {



        //Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, followerChannelID)

                .setSmallIcon(R.drawable.fields_logo_notification)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setColorized(true)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(defaultSoundUri);

         nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(2 /* ID of notification */, notificationBuilder.build());

    }


}
