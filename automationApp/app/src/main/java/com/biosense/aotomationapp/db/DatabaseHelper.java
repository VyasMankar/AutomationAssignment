package com.biosense.aotomationapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.biosense.aotomationapp.alarm.Alarm;
import com.biosense.aotomationapp.model.url_Info_model;
import com.biosense.aotomationapp.model.info_model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "url_db";

    //Table Name
    public static final String TABLE_URLINFO = "table_url_info";


    //URLinfo
    public static final String KEY_BASE_URL_TXT= "baseurl";
    public static final String KEY_NODE= "node";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_STATUS = "status";
    public static final String KEY_STATUS_FLAG = "flag";
    public static final String KEY_UUID = "uuid";
    public static final String KEY_RAWID = "rawid";
    public static final String KEY_KEY1 = "key1";
    public static final String KEY_VALUE1 = "value1";
    public static final String KEY_KEY2 = "key2";
    public static final String KEY_VALUE2 = "value2";


    public static final String ALARM_TABLE = "alarm";
    public static final String COLUMN_ALARM_ID = "_id";
    public static final String COLUMN_ALARM_ACTIVE = "alarm_active";
    public static final String COLUMN_ALARM_TIME = "alarm_time";
    public static final String COLUMN_ALARM_DAYS = "alarm_days";
    public static final String COLUMN_ALARM_DIFFICULTY = "alarm_difficulty";
    public static final String COLUMN_ALARM_TONE = "alarm_tone";
    public static final String COLUMN_ALARM_VIBRATE = "alarm_vibrate";
    public static final String COLUMN_ALARM_NAME = "alarm_name";

    //Data type
    private static final String TEXT = "TEXT";
    private static final String INTEGERPRIMARYKEY = "INTEGER PRIMARY KEY";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        List<info_model> patientInfo_models = new ArrayList<info_model>();
        addData(patientInfo_models, KEY_RAWID, INTEGERPRIMARYKEY);
        addData(patientInfo_models, KEY_UUID, TEXT);
        addData(patientInfo_models, KEY_BASE_URL_TXT, TEXT);
        addData(patientInfo_models, KEY_NODE, TEXT);
        addData(patientInfo_models, KEY_DATE, TEXT);
        addData(patientInfo_models, KEY_TIME, TEXT);
        addData(patientInfo_models, KEY_STATUS, TEXT);
        addData(patientInfo_models, KEY_STATUS_FLAG, TEXT);
        addData(patientInfo_models, KEY_KEY1, TEXT);
        addData(patientInfo_models, KEY_VALUE1, TEXT);
        addData(patientInfo_models, KEY_KEY2, TEXT);
        addData(patientInfo_models, KEY_VALUE2, TEXT);

        db.execSQL(makeTable(TABLE_URLINFO, patientInfo_models));

        String ALARM_TABLE_Q = "CREATE TABLE IF NOT EXISTS " + ALARM_TABLE + " ( "
                + COLUMN_ALARM_ID + " INTEGER primary key autoincrement, "
                + COLUMN_ALARM_ACTIVE + " INTEGER NOT NULL, "
                + COLUMN_ALARM_TIME + " TEXT NOT NULL, "
                + COLUMN_ALARM_DAYS + " BLOB NOT NULL, "
                + COLUMN_ALARM_DIFFICULTY + " INTEGER NOT NULL, "
                + COLUMN_ALARM_TONE + " TEXT NOT NULL, "
                + COLUMN_ALARM_VIBRATE + " INTEGER NOT NULL, "
                + COLUMN_ALARM_NAME + " TEXT NOT NULL)";
        db.execSQL(ALARM_TABLE_Q);
    }


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_URLINFO);
        // Create tables again
         onCreate(db);
    }


    public void addToTable(List<info_model> d, String tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (info_model model : d)
        {
            if(model.getDbCol().contains("rawid"))
            {
                if(model.getData() != null)
                    values.put(model.getDbCol(), Integer.parseInt(model.getData()));
            }
            else
            {
                values.put(model.getDbCol(), model.getData());
            }
        }
        db.insert(tableName, null, values);
        db.close();
    }



    public url_Info_model getUrlData(String uuid)
    {
        url_Info_model patientInfo_model = new url_Info_model();
        String selectQuery = "SELECT * FROM " + TABLE_URLINFO +" where " +KEY_UUID +" = "+uuid;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {
            do
            {
                patientInfo_model=getCursorObjPatientInfo(cursor);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return patientInfo_model;
    }


    public List<url_Info_model> getUrlData()
    {
        List<url_Info_model> url_Info_models = new ArrayList<url_Info_model>();
        String selectQuery = "SELECT * FROM " + TABLE_URLINFO;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {
            do
            {
                url_Info_models.add(getCursorObjPatientInfo(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return url_Info_models;
    }

    public url_Info_model getCursorObjPatientInfo(Cursor cursor)
    {
        url_Info_model URLInfo_model = new url_Info_model();
        URLInfo_model.setRawid(Integer.parseInt(cursor.getString(0)));
        URLInfo_model.setUuid(cursor.getString(1));
        URLInfo_model.setBaseurl(cursor.getString(2));
        URLInfo_model.setNode(cursor.getString(3));
        URLInfo_model.setDate(cursor.getString(4));
        URLInfo_model.setTime(cursor.getString(5));
        URLInfo_model.setStatus(cursor.getString(6));
        URLInfo_model.setFlag(cursor.getString(7));
        URLInfo_model.setKey1(cursor.getString(8));
        URLInfo_model.setValue1(cursor.getString(9));
        URLInfo_model.setKey2(cursor.getString(10));
        URLInfo_model.setValue2(cursor.getString(11));
        return URLInfo_model;
    }



    public String makeTable(String tablename, List<info_model> list)
    {
        int k = 0;
        String mainString = "CREATE TABLE IF NOT EXISTS " + tablename + "(";
        for (info_model modelClass : list)
        {
            if (k == list.size() - 1)
            {
                mainString = mainString + modelClass.getColName() + " " + modelClass.getDataType();
            } else
            {
                mainString = mainString + modelClass.getColName() + " " + modelClass.getDataType() + ",";
            }
            k++;
        }
        mainString = mainString + ");";
        return mainString;
    }

    public void addData(List<info_model> list, String colName, String dataType)
    {
        info_model modelClass = new info_model();
        modelClass.setColName(colName);
        modelClass.setDataType(dataType);
        list.add(modelClass);
    }

    public List<Alarm> getAllAlarm()
    {
        List<Alarm> alarms = new ArrayList<Alarm>();
        // List<DbModel> resultdataList = new ArrayList<DbModel>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + ALARM_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {

            do
            {

                Alarm alarm = new Alarm();
                alarm.setId(cursor.getInt(0));
                alarm.setAlarmActive(cursor.getInt(1) == 1);
                alarm.setAlarmTime(cursor.getString(2));
                byte[] repeatDaysBytes = cursor.getBlob(3);

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                        repeatDaysBytes);
                try
                {
                    ObjectInputStream objectInputStream = new ObjectInputStream(
                            byteArrayInputStream);
                    Alarm.Day[] repeatDays;
                    Object object = objectInputStream.readObject();
                    if (object instanceof Alarm.Day[])
                    {
                        repeatDays = (Alarm.Day[]) object;
                        alarm.setDays(repeatDays);
                    }
                } catch (StreamCorruptedException e)
                {
                    e.printStackTrace();
                } catch (IOException e)
                {
                    e.printStackTrace();
                } catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }

                alarm.setDifficulty("EASY");
                alarm.setAlarmTonePath(cursor.getString(5));
                alarm.setVibrate(cursor.getInt(6) == 1);
                alarm.setAlarmName(cursor.getString(7));

                alarms.add(alarm);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return alarms;
    }


    public void updateURLStatus(String mStatus,String uuid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_URLINFO + " SET " + KEY_STATUS + "='" + mStatus + "' where " + KEY_UUID+ " = '" + uuid + "'";
        db.execSQL(query);
        db.close();

    }

}
