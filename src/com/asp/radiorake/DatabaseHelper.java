package com.asp.radiorake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.asp.radiorake.utils.DateUtils;
import com.asp.radiorake.utils.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "db";

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public static final String FAVOURITES_TABLE = "stations";
    public static final String FAVOURITES_ID = "_id";
    public static final String FAVOURITES_NAME = "name";
    public static final String FAVOURITES_URL = "url";

    public static final String RECORDING_TYPES_TABLE = "recording_type";
    public static final String RECORDING_TYPES_ID = "_id";
    public static final String RECORDING_TYPES_TYPE = "type";

    public static final String STATIONS_TABLE = "stations";
    public static final String STATIONS_NAME = "name";
    public static final String STATIONS_URL = "url";

    private static final String SCHEDULED_RECORDINGS_TABLE = "recording_schedule";
    private static final String SCHEDULED_RECORDINGS_ID = "_id";
    public static final String SCHEDULED_RECORDINGS_START_TIME = "start_time";
    public static final String SCHEDULED_RECORDINGS_END_TIME = "end_time";
    public static final String SCHEDULED_RECORDINGS_STATION = "station";
    public static final String SCHEDULED_RECORDINGS_TYPE = "type";

    private static final String TAG = "com.asp.radiorake.DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    // Creates a empty database on the system and rewrites it with your own database.
    private void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database - " + e.getMessage());
            }
        }
    }

    // Check if the database already exist to avoid re-copying the file each time you open the application.
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {

            String myDB = myContext.getDatabasePath(DB_NAME).toString();
            checkDB = SQLiteDatabase.openDatabase(myDB, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
            Log.e(TAG, "SQLException occurred in checkDataBase()", e);
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    // Copies your database from your local assets-folder to the just created empty database in the
    // system folder, from where it can be accessed and handled.This is done by transfering bytestream.
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = myContext.getDatabasePath(DB_NAME).toString();

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws IOException {
        //Open the database
        createDataBase();
        String myDB = myContext.getDatabasePath(DB_NAME).toString();
        myDataBase = SQLiteDatabase.openDatabase(myDB, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public RadioDetails getFavourite(long id) {
        RadioDetails radioDetails = null;
        Cursor cursor = myDataBase.query(FAVOURITES_TABLE, new String[]{FAVOURITES_ID, FAVOURITES_NAME, FAVOURITES_URL}, "_ID = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            radioDetails = new RadioDetails(cursor.getInt(0), cursor.getString(1), cursor.getString(2), null);
        }
        return radioDetails;
    }

    public Cursor getFavourites() {
        return myDataBase.query(FAVOURITES_TABLE, new String[]{FAVOURITES_ID, FAVOURITES_NAME, FAVOURITES_URL}, null, null, null, null, null);
    }

    public void addFavourite(RadioDetails radioDetails) {
        Log.d(TAG, "Attempting to add: " + radioDetails);
        ContentValues contentValues = new ContentValues();
        contentValues.put(FAVOURITES_NAME, radioDetails.getStationName());
        if (StringUtils.IsNullOrEmpty(radioDetails.getPlaylistUrl())) {
            contentValues.put(FAVOURITES_URL, radioDetails.getStreamUrl());
        } else {
            contentValues.put(FAVOURITES_URL, radioDetails.getPlaylistUrl());
        }
        myDataBase.insert(FAVOURITES_TABLE, FAVOURITES_NAME, contentValues);
    }

    public void deleteFavourite(long id) {
        myDataBase.delete(FAVOURITES_TABLE, "_id=?", new String[]{Long.toString(id)});
    }

    public void updateFavourite(RadioDetails radioDetails) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FAVOURITES_NAME, radioDetails.getStationName());
        contentValues.put(FAVOURITES_URL, StringUtils.IsNullOrEmpty(radioDetails.getPlaylistUrl()) ? radioDetails.getStreamUrl() : radioDetails.getPlaylistUrl());
        myDataBase.update(FAVOURITES_TABLE, contentValues, "_id=?", new String[]{Long.toString(radioDetails.getId())});
    }

    public Cursor getRecordingTypes() {
        return myDataBase.query(RECORDING_TYPES_TABLE, new String[]{RECORDING_TYPES_ID, RECORDING_TYPES_TYPE}, null, null, null, null, null);
    }

    public long insertScheduledRecording(long startTime, long endTime, int station, int type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SCHEDULED_RECORDINGS_START_TIME, startTime);
        contentValues.put(SCHEDULED_RECORDINGS_END_TIME, endTime);
        contentValues.put(SCHEDULED_RECORDINGS_STATION, station);
        contentValues.put(SCHEDULED_RECORDINGS_TYPE, type);
        return myDataBase.insert(SCHEDULED_RECORDINGS_TABLE, SCHEDULED_RECORDINGS_START_TIME, contentValues);
    }

    public void deleteScheduledRecording(long id) {
        myDataBase.delete(SCHEDULED_RECORDINGS_TABLE, "_id=?", new String[]{Long.toString(id)});
    }


    public void updateScheduledRecording(long scheduledRecordingId, long startDateTime, long endDateTime, int station, int type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SCHEDULED_RECORDINGS_STATION, station);
        contentValues.put(SCHEDULED_RECORDINGS_TYPE, type);
        contentValues.put(SCHEDULED_RECORDINGS_START_TIME, startDateTime);
        contentValues.put(SCHEDULED_RECORDINGS_START_TIME, startDateTime);
        contentValues.put(SCHEDULED_RECORDINGS_END_TIME, endDateTime);
        myDataBase.update(SCHEDULED_RECORDINGS_TABLE, contentValues, "_id=?", new String[]{Long.toString(scheduledRecordingId)});
    }

    public void updateScheduledRecordingAddDay(long databaseId) {
        ScheduledRecording scheduledRecording = GetScheduledRecording(databaseId);

        if (scheduledRecording != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SCHEDULED_RECORDINGS_START_TIME, DateUtils.addDay(scheduledRecording.startDateTime));
            contentValues.put(SCHEDULED_RECORDINGS_END_TIME, DateUtils.addDay(scheduledRecording.endDateTime));
            myDataBase.update(SCHEDULED_RECORDINGS_TABLE, contentValues, "_id=?", new String[]{Long.toString(databaseId)});
        }
    }

    public ScheduledRecording GetScheduledRecording(long databaseId) {

        Cursor cursor = myDataBase.query(SCHEDULED_RECORDINGS_TABLE,
                new String[]{SCHEDULED_RECORDINGS_STATION,
                        SCHEDULED_RECORDINGS_TYPE,
                        SCHEDULED_RECORDINGS_START_TIME,
                        SCHEDULED_RECORDINGS_END_TIME},
                " _ID = ?",
                new String[]{String.valueOf(databaseId)}, null, null, null);

        ScheduledRecording scheduledRecording = new ScheduledRecording();

        if (cursor.moveToFirst()) {
            scheduledRecording.station = cursor.getInt(0);
            scheduledRecording.type = cursor.getInt(1);
            scheduledRecording.startDateTime = cursor.getLong(2);
            scheduledRecording.endDateTime = cursor.getLong(3);
        }

        cursor.close();
        return scheduledRecording;
    }

    public void updateScheduledRecordingAddWeek(long databaseId) {
        ScheduledRecording scheduledRecording = GetScheduledRecording(databaseId);

        if (scheduledRecording != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SCHEDULED_RECORDINGS_START_TIME, DateUtils.addWeek(scheduledRecording.startDateTime));
            contentValues.put(SCHEDULED_RECORDINGS_END_TIME, DateUtils.addWeek(scheduledRecording.endDateTime));
            myDataBase.update(SCHEDULED_RECORDINGS_TABLE, contentValues, "_id=?", new String[]{Long.toString(databaseId)});
        }
    }

    public Cursor getScheduledRecordingsList() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT RECORDING_SCHEDULE._ID, STATIONS.NAME, RECORDING_TYPE.TYPE, RECORDING_SCHEDULE.START_TIME, RECORDING_SCHEDULE.END_TIME ");
        query.append("FROM RECORDING_SCHEDULE, RECORDING_TYPE, STATIONS ");
        query.append("WHERE RECORDING_SCHEDULE.STATION = STATIONS._ID AND RECORDING_SCHEDULE.TYPE = RECORDING_TYPE._ID ");
        query.append("ORDER BY RECORDING_SCHEDULE.START_TIME");
        return myDataBase.rawQuery(query.toString(), new String[]{});
    }

    public Cursor getAllScheduledRecordings() {
        String[] columns = {
                SCHEDULED_RECORDINGS_ID,
                SCHEDULED_RECORDINGS_START_TIME,
                SCHEDULED_RECORDINGS_END_TIME,
                SCHEDULED_RECORDINGS_STATION,
                SCHEDULED_RECORDINGS_TYPE};
        return myDataBase.query(SCHEDULED_RECORDINGS_TABLE, columns, null, null, null, null, null);
    }

    public RadioDetails getRadioDetail(long stationId) {
        Cursor cursor = myDataBase.query(STATIONS_TABLE, new String[]{STATIONS_NAME, STATIONS_URL}, " _ID = ?", new String[]{String.valueOf(stationId)}, null, null, null);
        RadioDetails radioDetails = new RadioDetails();
        if (cursor.moveToFirst()) {
            radioDetails.setStationName(cursor.getString(0));
            radioDetails.setPlaylistUrl(cursor.getString(1));
        }
        cursor.close();
        return radioDetails;
    }

    public class ScheduledRecording {
        public int station;
        public int type;
        public long startDateTime;
        public long endDateTime;
    }
}
