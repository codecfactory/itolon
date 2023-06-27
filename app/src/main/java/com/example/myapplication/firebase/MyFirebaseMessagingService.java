package com.example.myapplication.firebase;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.prefrences.SharedPref;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    NotificationManager notifManager;
    NotificationChannel mChannel;
    Intent intent;
    boolean flag=true;
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.e("newToken", token);
//Add your token in your sharepreferences.
//        SharedPref.write(SharedPref.REFRESH_TOKEN,token);
//        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fcm_token", token).apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getNotification() != null)
            {
                if(remoteMessage.getNotification() != null&&remoteMessage.getNotification().getTitle().equals("New message received")) {
                    Intent intent = new Intent();
                    intent.putExtra("extra", remoteMessage.getData().get("thread_id"));
                    intent.putExtra("badge", remoteMessage.getData().get("badge"));
                    intent.setAction("com.tagalong.app.onMessageReceived");
                    sendBroadcast(intent);
                    Intent intent1 = new Intent();
                    intent1.putExtra("badge", remoteMessage.getData().get("badge"));
                    intent1.setAction("com.tagalong.app.onBadgeReceived");
                    sendBroadcast(intent1);
                    flag=true;
                    displayCustomNotification("TAG-ALONG", remoteMessage.getNotification().getBody(),remoteMessage.getData().get("thread_id"));
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("badge", remoteMessage.getData().get("badge"));
                    intent.setAction("com.tagalong.app.onBadgeReceived");
                    sendBroadcast(intent);
                    flag=false;
                    displayCustomNotification("TAG-ALONG", remoteMessage.getNotification().getBody(),"");
                }
            }
        }
    }
    @SuppressLint("WrongConstant")
    private void displayCustomNotification(String title, String description, String threadId) {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(flag) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("thread_id",threadId);
        }else{
            intent = new Intent(this, MainActivity.class);
        }
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel("0", description, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, "0");
            intent.putExtra("thread_id",threadId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentTitle(title).setContentText(description).setSmallIcon(getNotificationIcon()).setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.itolon_logo)).setBadgeIconType(R.drawable.itolon_logo).setContentIntent(pendingIntent).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            Notification notification = builder.build();
            notifManager.notify(0, notification);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("thread_id",threadId);
            PendingIntent pendingIntent = null;
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setContentTitle(title).setContentText(description).setAutoCancel(true).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.itolon_logo)).setColor(ContextCompat.getColor(getBaseContext(), R.color.white)).setSound(defaultSoundUri).setSmallIcon(getNotificationIcon()).setContentIntent(pendingIntent).setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(description));
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1251, notificationBuilder.build());
        }
    }
    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.itolon_logo : R.drawable.itolon_logo;
    }
}