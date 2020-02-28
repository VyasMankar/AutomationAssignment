package com.biosense.aotomationapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.biosense.aotomationapp.alarm.Alarm;
import com.biosense.aotomationapp.alarm.AlarmAlertBroadcastReciever;
import com.biosense.aotomationapp.R;
import com.biosense.aotomationapp.db.DatabaseHelper;
import com.biosense.aotomationapp.helper.Util;
import com.biosense.aotomationapp.model.info_model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Add_Url extends AppCompatActivity {

    int Year,Month,Day,Hour,Minute;
    EditText edtAddBaseURL,edtTime,edtDate,edtNode,edtKey1,edtKey2,edtValue1,edtValue2;
    Alarm alarm;
    Calendar newAlarmTime;
    DatabaseHelper databaseHelper;
    Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__url);
        alarm = new Alarm();
        newAlarmTime = Calendar.getInstance();
        databaseHelper=new DatabaseHelper(Add_Url.this);
        util = new Util(Add_Url.this);

        edtAddBaseURL = (EditText) findViewById(R.id.baseURL);
        edtNode = (EditText) findViewById(R.id.node);
        edtTime   = (EditText) findViewById(R.id.edtTime);
        edtDate   = (EditText) findViewById(R.id.edtDate);

        edtKey1 = (EditText) findViewById(R.id.key1);
        edtKey2 = (EditText) findViewById(R.id.key2);
        edtValue1   = (EditText) findViewById(R.id.value1);
        edtValue2   = (EditText) findViewById(R.id.value2);

    }

    public void onAddUrl(View view) {

        if(isValidate()) {

            //Calender Object for setting alarm
            Calendar objCalendar = Calendar.getInstance();
            objCalendar.set(Calendar.YEAR, Year);
            objCalendar.set(Calendar.MONTH, Month - 1);
            objCalendar.set(Calendar.DAY_OF_MONTH, Day);

            objCalendar.set(Calendar.MINUTE, Minute);
            objCalendar.set(Calendar.SECOND, 0);
            objCalendar.set(Calendar.MILLISECOND, 0);

            if (Hour >= 12)
                objCalendar.set(Calendar.HOUR_OF_DAY, Hour - 12);
            else
                objCalendar.set(Calendar.HOUR_OF_DAY, Hour);

            if (Hour >= 12)
                objCalendar.set(Calendar.AM_PM, Calendar.PM);
            else
                objCalendar.set(Calendar.AM_PM, Calendar.AM);


            //Set Alarm Object
            alarm.setAlarmActive(true);
            alarm.setVibrate(true);
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            assert vibrator != null;
            vibrator.vibrate(1000);
            alarm.setAlarmName("Automator");

            String uuid = util.generateUUID();

            //Set Pending Intent
            Intent myIntent = new Intent(Add_Url.this, AlarmAlertBroadcastReciever.class);
            myIntent.putExtra("uuid", uuid + "");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(Add_Url.this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) Add_Url.this.getSystemService(Context.ALARM_SERVICE);


            if (Build.VERSION.SDK_INT >= 23) {
                assert alarmManager != null;
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, objCalendar.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                assert alarmManager != null;
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, objCalendar.getTimeInMillis(), pendingIntent);
            } else {
                assert alarmManager != null;
                alarmManager.set(AlarmManager.RTC_WAKEUP, objCalendar.getTimeInMillis(), pendingIntent);
            }


            //Add to database
            List<info_model> anm_db_datatype_modelList = new ArrayList<info_model>();
            util.setDynamic(DatabaseHelper.KEY_UUID, uuid, anm_db_datatype_modelList);
            util.setDynamic(DatabaseHelper.KEY_BASE_URL_TXT, edtAddBaseURL.getText().toString(), anm_db_datatype_modelList);
            util.setDynamic(DatabaseHelper.KEY_NODE, edtNode.getText().toString(), anm_db_datatype_modelList);
            util.setDynamic(DatabaseHelper.KEY_DATE, edtDate.getText().toString(), anm_db_datatype_modelList);
            util.setDynamic(DatabaseHelper.KEY_TIME, edtTime.getText().toString(), anm_db_datatype_modelList);
            util.setDynamic(DatabaseHelper.KEY_STATUS_FLAG, "NA", anm_db_datatype_modelList);
            util.setDynamic(DatabaseHelper.KEY_STATUS, "pending", anm_db_datatype_modelList);
            util.setDynamic(DatabaseHelper.KEY_KEY1, edtKey1.getText().toString(), anm_db_datatype_modelList);
            util.setDynamic(DatabaseHelper.KEY_VALUE1, edtValue1.getText().toString(), anm_db_datatype_modelList);
            util.setDynamic(DatabaseHelper.KEY_KEY2, edtKey2.getText().toString(), anm_db_datatype_modelList);
            util.setDynamic(DatabaseHelper.KEY_VALUE2, edtValue1.getText().toString(), anm_db_datatype_modelList);

            databaseHelper.addToTable(anm_db_datatype_modelList, DatabaseHelper.TABLE_URLINFO);

            startActivity(new Intent(this,Home.class));
            finish();
        }
        else
        {
            Toast.makeText(Add_Url.this,"All fields are mandatory",Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidate() {
        if(edtAddBaseURL.getText().toString().equals(""))
        {
            return false;
        }

        if(edtDate.getText().toString().equals(""))
        {
            return false;
        }

        if(edtTime.getText().toString().equals(""))
        {
            return false;
        }

        return true;
    }

    public void onClickTime(View view) {

        TimePickerDialog timePickerDialog = new TimePickerDialog(Add_Url.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {

                newAlarmTime.set(Calendar.HOUR_OF_DAY, hours);
                newAlarmTime.set(Calendar.MINUTE, minutes);
                newAlarmTime.set(Calendar.SECOND, 0);
                Hour = hours;
                Minute = minutes;
                edtTime.setText(hours +":"+minutes);
            }
        },
                alarm.getAlarmTime().get(Calendar.HOUR_OF_DAY), alarm.getAlarmTime().get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Automator Time");
        timePickerDialog.show();
    }

    public void onClickDate(View view) {
        final int mYear = newAlarmTime.get(newAlarmTime.YEAR);
        final int mMonth = newAlarmTime.get(newAlarmTime.MONTH);
        final int mDay = newAlarmTime.get(newAlarmTime.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(Add_Url.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth = selectedmonth + 1;
                Year = selectedyear;
                Month = selectedmonth;
                Day = selectedday;
                edtDate.setText(Day+"/"+Month+"/"+Year);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,Home.class));
        finish();
    }
}
