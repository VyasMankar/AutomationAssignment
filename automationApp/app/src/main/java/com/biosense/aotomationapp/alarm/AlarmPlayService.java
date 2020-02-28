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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


public class AlarmPlayService extends Service
    {
    private Alarm alarm;
    private MediaPlayer mediaPlayer;

    private StringBuilder answerBuilder = new StringBuilder();

    private Vibrator vibrator;

    private boolean alarmActive;

    @Override
    public IBinder onBind(Intent intent)
        {

            return null;
        }

    @Override
    public void onCreate()
        {
            super.onCreate();
           // Toast.makeText(getApplicationContext(), "Created", Toast.LENGTH_SHORT).show();

        }

    @Override
    public void onDestroy()
        {
            super.onDestroy();
            try
                {
                    if (vibrator != null)
                        {
                            vibrator.cancel();
                        }
                }
            catch (Exception e)
                {

                }
            try
                {
                    mediaPlayer.stop();
                }
            catch (Exception e)
                {

                }
            try
                {
                    mediaPlayer.release();
                }
            catch (Exception e)
                {

                }
          //  Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_SHORT).show();
        }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
        {

            Bundle bundle = intent.getExtras();
            alarm = (Alarm) bundle.getSerializable("alarm");
            startAlarm();
            return START_NOT_STICKY;
        }

    private void startAlarm()
        {
            TelephonyManager telephonyManager = (TelephonyManager) this
                    .getSystemService(Context.TELEPHONY_SERVICE);

            PhoneStateListener phoneStateListener = new PhoneStateListener()
                {
                @Override
                public void onCallStateChanged(int state, String incomingNumber)
                    {
                        switch (state)
                            {
                                case TelephonyManager.CALL_STATE_RINGING:
                                    Log.d(getClass().getSimpleName(), "Incoming call: "
                                            + incomingNumber);
                                    try
                                        {
                                            mediaPlayer.pause();
                                        }
                                    catch (IllegalStateException e)
                                        {

                                        }
                                    break;
                                case TelephonyManager.CALL_STATE_IDLE:
                                    Log.d(getClass().getSimpleName(), "Call State Idle");
                                    try
                                        {
                                            mediaPlayer.start();
                                        }
                                    catch (IllegalStateException e)
                                        {

                                        }
                                    break;
                            }
                        super.onCallStateChanged(state, incomingNumber);
                    }
                };

            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE);


            if (alarm.getAlarmTonePath() != "")
                {
                    mediaPlayer = new MediaPlayer();
                    if (alarm.getVibrate())
                        {
                            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                            long[] pattern = {1000, 200, 200, 200};
                            vibrator.vibrate(pattern, 0);
                        }
                    try
                        {
                            mediaPlayer.setVolume(1.0f, 1.0f);
                            mediaPlayer.setDataSource(this,
                                    Uri.parse(alarm.getAlarmTonePath()));
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                            mediaPlayer.setLooping(true);
                            mediaPlayer.prepare();
                            mediaPlayer.start();

                        }
                    catch (Exception e)
                        {
                            mediaPlayer.release();
                            alarmActive = false;
                        }
                }

        }
    }
