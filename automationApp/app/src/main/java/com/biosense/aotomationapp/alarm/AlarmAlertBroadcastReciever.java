/* Copyright 2014 Sheldon Neilson www.neilson.co.za
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.biosense.aotomationapp.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.biosense.aotomationapp.R;
import com.biosense.aotomationapp.db.DatabaseHelper;
import com.biosense.aotomationapp.helper.Util;
import com.biosense.aotomationapp.activities.Home;
import com.biosense.aotomationapp.model.url_Info_model;


public class AlarmAlertBroadcastReciever extends BroadcastReceiver {

    @RequiresApi(26)
    private NotificationUtils mNotificationUtils;

    NotificationCompat.Builder notification;
    private static final int uniqID = 45612;
    String CUSTOM_INTENT_SNOOZE = "custom.action.for.alarm.snooze";
    String CUSTOM_INTENT_DISMISS = "custom.action.for.alarm.dismiss";
    String CUSTOM_INTENT_DELETE = "custom.action.for.alarm.delete";
    String CUSTOM_INTENT_CLICK = "custom.action.for.alarm.click";
    private Alarm alarm;
    private MediaPlayer mediaPlayer;
    private StringBuilder answerBuilder = new StringBuilder();
    private Vibrator vibrator;
    private boolean alarmActive;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent)
    {

        String uuid=intent.getStringExtra("uuid");

        DatabaseHelper db = new DatabaseHelper(context);
        url_Info_model url_info_model = db.getUrlData(uuid);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            AsyncTaskRunner runner = new AsyncTaskRunner(context,url_info_model,db);
            runner.execute("URL");

            mNotificationUtils = new NotificationUtils(context);

            Notification.Builder nb = mNotificationUtils.
                    getAndroidChannelNotification("Automator", "You have a reminder for url hit");

            mNotificationUtils.getManager().notify(101, nb.build());


            Intent resultIntent = new Intent(context, Home.class);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            nb.setContentIntent(pendingIntent);

            int notificationId = 101;

            NotificationManager notifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notifyMgr.notify(notificationId, nb.build());

        }
        else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.N){


            Intent mathAlarmServiceIntent = new Intent(
                    context,
                    AlarmServiceBroadcastReciever.class);
            context.sendBroadcast(mathAlarmServiceIntent, null);

            StaticWakeLock.lockOn(context);


            AsyncTaskRunner runner = new AsyncTaskRunner(context,url_info_model,db);
            runner.execute("URL");


            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.notification_)
                            .setContentTitle("You have one notification from Automator")
                            .setContentText("Automator Reminder");

            Intent resultIntent = new Intent(context, Home.class);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            builder.setContentIntent(pendingIntent);

            int notificationId = 101;

            NotificationManager notifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notifyMgr.notify(notificationId, builder.build());
        }

        else {

            AsyncTaskRunner runner = new AsyncTaskRunner(context,url_info_model,db);
            runner.execute("URL");

            StaticWakeLock.lockOn(context);
            Bundle bundle = intent.getExtras();
            Alarm alarm = (Alarm) bundle.getSerializable("alarm");
            this.context = context;
            Intent serviceIntent = new Intent(context, AlarmPlayService.class);
            serviceIntent.putExtra("alarm", alarm);
            context.startService(serviceIntent);

            notification = new NotificationCompat.Builder(context);
            notification.setAutoCancel(true);
            notification.setSmallIcon(R.mipmap.ic_launcher);
            notification.setTicker("You have one notification from Automator");
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle("You have a reminder for Upcoming Automator");
            notification.setContentText("Automator Reminder");


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(uniqID, notification.build());

        }

    }



    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        Context context;
        url_Info_model url_info_model;
        DatabaseHelper db;

        public AsyncTaskRunner(Context context,url_Info_model url_info_model,DatabaseHelper db) {
            this.context = context;
            this.url_info_model=url_info_model;
            this.db= db;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Util util =new Util(context);

                if(util.isNetworkAvailable()) {
                    resp = util.sendJSONPOST2(url_info_model.getBaseurl(), url_info_model.getNode()
                            , url_info_model.getKey1(), url_info_model.getValue1(), url_info_model.getKey2(), url_info_model.value2);
                }else
                {
                    return "network issue";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            db.updateURLStatus(result,url_info_model.getUuid());
            Toast.makeText(context, "Result Saved in the local database", Toast.LENGTH_SHORT).show();
        }

    }

}