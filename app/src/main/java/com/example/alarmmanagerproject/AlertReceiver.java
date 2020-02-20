package com.example.alarmmanagerproject;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.alarmmanagerproject.home.HomeActivity;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        if(intent.getAction().equals("Hello")){
//            Log.d("我是MyBroadcastReceiver1"  , "我聽到了喔");
//        }
        /* 邏輯只能十秒 請寫在service裡面*/
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        nb.setContentIntent(contentIntent);
        notificationHelper.getManager().notify(1, nb.build());
    }
}