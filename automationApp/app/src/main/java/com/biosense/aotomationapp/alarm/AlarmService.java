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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.biosense.aotomationapp.db.DatabaseHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;




public class AlarmService extends Service
    {
    DatabaseHelper db;

    @Override
    public IBinder onBind(Intent intent)
        {

            return null;
        }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate()
        {
            Log.d(this.getClass().getSimpleName(), "onCreate()");
            super.onCreate();
            db = new DatabaseHelper(getApplicationContext());
        }

    private Alarm getNext()
        {
            Set<Alarm> alarmQueue = new TreeSet<Alarm>(new Comparator<Alarm>()
                {
                @Override
                public int compare(Alarm lhs, Alarm rhs)
                    {
                        int result = 0;
                        long diff = lhs.getAlarmTime().getTimeInMillis() - rhs.getAlarmTime().getTimeInMillis();
                        if (diff > 0)
                            {
                                return 1;
                            }
                        else if (diff < 0)
                            {
                                return -1;
                            }
                        return result;
                    }
                });


            List<Alarm> alarms = db.getAllAlarm();

            for (Alarm alarm : alarms)
                {
                    if (alarm.getAlarmActive())
                        {
                            alarmQueue.add(alarm);
                        }
                }
            if (alarmQueue.iterator().hasNext())
                {
                    return alarmQueue.iterator().next();
                }
            else
                {
                    return null;
                }
        }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy()
        {
            super.onDestroy();
        }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
        {
            Log.d(this.getClass().getSimpleName(), "onStartCommand()");
            Alarm alarm;
            if (intent.getIntExtra("flag", -1) != 1)
                {
                    alarm = getNext();
                }
            else
                {
                    Bundle bundle = intent.getExtras();
                    alarm = (Alarm) bundle.getSerializable("alarm");
                }
            if (null != alarm)
                {
                    alarm.schedule(getApplicationContext());
                    Log.d(this.getClass().getSimpleName(), alarm.getTimeUntilNextAlarmMessage());

                }
            else
                {
                    Intent myIntent = new Intent(getApplicationContext(), AlarmAlertBroadcastReciever.class);
                    myIntent.putExtra("alarm", new Alarm());

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

                    alarmManager.cancel(pendingIntent);
                }


            return START_NOT_STICKY;
        }

    }
