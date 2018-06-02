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

    public static final String inAppChannelID = "inAppChannel";
    public static final String inAppChannelName = "In App";
    public static final String followerChannelID = "followerChannel";
    public static final String followerChannelName = "New Followers";
    public static final String friendTrainingChannelID = "friendTrainingChannel";
    public static final String friendTrainingChannelName = "Friend's training";
    public static final String userMessageChannelID = "userMessageChannel";
    public static final String userMessageChannelName = "User's messages";
    public static final String teamMessageChannelID = "teamMessageChannel";
    public static final String teamMessageChannelName = "Team's messages";
    NotificationManager nm;

    @TargetApi(Build.VERSION_CODES.O)
    public void setupChannels(){

        NotificationChannel inAppChannel = new NotificationChannel(inAppChannelID,
                inAppChannelName, NotificationManager.IMPORTANCE_HIGH);
        inAppChannel.enableLights(true);
        inAppChannel.enableVibration(false);
        inAppChannel.setLightColor(R.color.colorPrimary);
        inAppChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(inAppChannel);

        NotificationChannel followerChannel = new NotificationChannel(followerChannelID,
                followerChannelName, NotificationManager.IMPORTANCE_HIGH);
        followerChannel.enableLights(true);
        followerChannel.enableVibration(true);
        followerChannel.setLightColor(R.color.colorPrimary);
        followerChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(followerChannel);

        NotificationChannel friendTrainingChannel = new NotificationChannel(friendTrainingChannelID,
                friendTrainingChannelName, NotificationManager.IMPORTANCE_HIGH);
        friendTrainingChannel.enableLights(true);
        friendTrainingChannel.enableVibration(true);
        friendTrainingChannel.setLightColor(R.color.colorPrimary);
        friendTrainingChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(friendTrainingChannel);

        NotificationChannel userMessageChannel = new NotificationChannel(userMessageChannelID,
                userMessageChannelName, NotificationManager.IMPORTANCE_HIGH);
        userMessageChannel.enableLights(true);
        userMessageChannel.enableVibration(true);
        userMessageChannel.setLightColor(R.color.colorPrimary);
        userMessageChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(userMessageChannel);

        NotificationChannel teamMessageChannel = new NotificationChannel(teamMessageChannelID,
                teamMessageChannelName, NotificationManager.IMPORTANCE_HIGH);
        teamMessageChannel.enableLights(true);
        teamMessageChannel.enableVibration(true);
        teamMessageChannel.setLightColor(R.color.colorPrimary);
        teamMessageChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(teamMessageChannel);

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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, inAppChannelID)

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
