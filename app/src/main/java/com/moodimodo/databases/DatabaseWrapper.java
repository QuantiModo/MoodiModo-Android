package com.moodimodo.databases;


import android.app.backup.BackupManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.moodimodo.Global;
import com.moodimodo.things.MoodThing;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.moodimodo.databases.MoodDatabaseHelper.*;

@Deprecated
public class DatabaseWrapper {

    public static final String DATABASE_NAME = "Mood";
    public static final String TABLE_MOOD = "mood";

    public static final String COLUMN_TIMESTAMP_NAME = "timestamp";
    public static final String COLUMN_MOOD_NAME = "mood";
    public static final String COLUMN_MOOD_ACCURATE_NAME = "accurateMood";
    public static final String COLUMN_GUILTY_NAME = "guilty";
    public static final String COLUMN_ALERT_NAME = "alert";
    public static final String COLUMN_AFRAID_NAME = "afraid";
    public static final String COLUMN_EXCITED_NAME = "excited";
    public static final String COLUMN_IRRITABLE_NAME = "irritable";
    public static final String COLUMN_ASHAMED_NAME = "ashamed";
    public static final String COLUMN_ATTENTIVE_NAME = "attentive";
    public static final String COLUMN_HOSTILE_NAME = "hostile";
    public static final String COLUMN_ACTIVE_NAME = "active";
    public static final String COLUMN_NERVOUS_NAME = "nervous";
    public static final String COLUMN_INTERESTED_NAME = "interested";
    public static final String COLUMN_ENTHUSIASTIC_NAME = "enthusiastic";
    public static final String COLUMN_JITTERY_NAME = "jittery";
    public static final String COLUMN_STRONG_NAME = "strong";
    public static final String COLUMN_DISTRESSED_NAME = "distressed";
    public static final String COLUMN_DETERMINED_NAME = "determined";
    public static final String COLUMN_UPSET_NAME = "upset";
    public static final String COLUMN_PROUD_NAME = "proud";
    public static final String COLUMN_SCARED_NAME = "scared";
    public static final String COLUMN_INSPIRED_NAME = "inspired";
    public static final String COLUMN_NOTE_NAME = "note";
    public static final String COLUMN_NEED_UPDATE_NAME = "needsUpdate";
    public static final String COLUMN_ID_NAME = "id";

    public static final int COLUMN_TIMESTAMP = 0;
    public static final int COLUMN_MOOD = 1;
    public static final int COLUMN_MOOD_ACCURATE = 2;
    public static final int COLUMN_GUILTY = 3;
    public static final int COLUMN_ALERT = 4;
    public static final int COLUMN_AFRAID = 5;
    public static final int COLUMN_EXCITED = 6;
    public static final int COLUMN_IRRITABLE = 7;
    public static final int COLUMN_ASHAMED = 8;
    public static final int COLUMN_ATTENTIVE = 9;
    public static final int COLUMN_HOSTILE = 10;
    public static final int COLUMN_ACTIVE = 11;
    public static final int COLUMN_NERVOUS = 12;
    public static final int COLUMN_INTERESTED = 13;
    public static final int COLUMN_ENTHUSIASTIC = 14;
    public static final int COLUMN_JITTERY = 15;
    public static final int COLUMN_STRONG = 16;
    public static final int COLUMN_DISTRESSED = 17;
    public static final int COLUMN_DETERMINED = 18;
    public static final int COLUMN_UPSET = 19;
    public static final int COLUMN_PROUD = 20;
    public static final int COLUMN_SCARED = 21;
    public static final int COLUMN_INSPIRED = 22;
    public static final int COLUMN_NOTE = 23;
    public static final int COLUMN_NEED_UPDATE = 24;
    public static final int COLUMN_ID = 25;

    MoodDatabaseHelper mHelper;
    BackupManager backupManager;
    Context context;

    public DatabaseWrapper(Context context) {
        mHelper = new MoodDatabaseHelper(context);
        backupManager = new BackupManager(context);
        this.context = context;
    }

    /**
     * Delets a mood entry from the database given the id
     * @param id the identifier of the Mood
     */
    public void deleteById(final int id){
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_MOOD, COLUMN_ID_NAME + "=" + id, null);
        close();
    }

    public void deleteLast() {
        long timestamp = getLastMoodReportDate().getTime() / 1000;
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_MOOD,COLUMN_TIMESTAMP_NAME + "=" + String.valueOf(timestamp),null);
        close();
    }

    public void close() {
        mHelper.closeIfNeeded();
    }

    public SQLiteDatabase getWritableDatabase() {
        return mHelper.getWritableDatabase();
    }

    public void insert(Context context, MoodThing entry) {
        ArrayList<MoodThing> list = new ArrayList<>(1);
        list.add(entry);
        insert(context, list, true);
    }

    public void insert(Context context, ArrayList<MoodThing> entries, boolean sync) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        for (MoodThing entry : entries) {
            database.insertWithOnConflict(TABLE_MOOD, null, entry.toCV(), SQLiteDatabase.CONFLICT_REPLACE);
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        close();
        backupManager.dataChanged();
        if (sync) {
            requestSync(context);
        }
    }

    public Date getFirstMoodReportDate() {
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_MOOD + " ORDER BY " + COLUMN_TIMESTAMP_NAME + " ASC LIMIT 1", null);
        long timestamp = System.currentTimeMillis() / 1000;
        if (cursor.moveToFirst()) {
            timestamp = Long.valueOf(cursor.getString(COLUMN_TIMESTAMP));
        }
        cursor.close();
        close();
        return new Date(timestamp * 1000);
    }

    public Date getLastMoodReportDate() {
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_MOOD + " ORDER BY " + COLUMN_TIMESTAMP_NAME + " DESC LIMIT 1", null);
        long timestamp = System.currentTimeMillis() / 1000;
        if (cursor.moveToFirst()) {
            timestamp = Long.valueOf(cursor.getString(COLUMN_TIMESTAMP));
        }
        cursor.close();
        close();
        return new Date(timestamp * 1000);
    }

    private MoodThing readCursor(Cursor cursor) {
        long timestamp = cursor.getLong(COLUMN_TIMESTAMP);
        int[] ratings = new int[22];
        ratings[MoodThing.RATING_MOOD] = cursor.getInt(COLUMN_MOOD);
        ratings[MoodThing.RATING_ACCURATE_MOOD] = cursor.getInt(COLUMN_MOOD_ACCURATE);
        ratings[MoodThing.RATING_GUILTY] = cursor.getInt(COLUMN_GUILTY);
        ratings[MoodThing.RATING_ALERT] = cursor.getInt(COLUMN_ALERT);
        ratings[MoodThing.RATING_AFRAID] = cursor.getInt(COLUMN_AFRAID);
        ratings[MoodThing.RATING_EXCITED] = cursor.getInt(COLUMN_EXCITED);
        ratings[MoodThing.RATING_IRRITABLE] = cursor.getInt(COLUMN_IRRITABLE);
        ratings[MoodThing.RATING_ASHAMED] = cursor.getInt(COLUMN_ASHAMED);
        ratings[MoodThing.RATING_ATTENTIVE] = cursor.getInt(COLUMN_ATTENTIVE);
        ratings[MoodThing.RATING_HOSTILE] = cursor.getInt(COLUMN_HOSTILE);
        ratings[MoodThing.RATING_ACTIVE] = cursor.getInt(COLUMN_ACTIVE);
        ratings[MoodThing.RATING_NERVOUS] = cursor.getInt(COLUMN_NERVOUS);
        ratings[MoodThing.RATING_INTERESTED] = cursor.getInt(COLUMN_INTERESTED);
        ratings[MoodThing.RATING_ENTHUSIASTIC] = cursor.getInt(COLUMN_ENTHUSIASTIC);
        ratings[MoodThing.RATING_JITTERY] = cursor.getInt(COLUMN_JITTERY);
        ratings[MoodThing.RATING_STRONG] = cursor.getInt(COLUMN_STRONG);
        ratings[MoodThing.RATING_DISTRESSED] = cursor.getInt(COLUMN_DISTRESSED);
        ratings[MoodThing.RATING_DETERMINED] = cursor.getInt(COLUMN_DETERMINED);
        ratings[MoodThing.RATING_UPSET] = cursor.getInt(COLUMN_UPSET);
        ratings[MoodThing.RATING_PROUD] = cursor.getInt(COLUMN_PROUD);
        ratings[MoodThing.RATING_SCARED] = cursor.getInt(COLUMN_SCARED);
        ratings[MoodThing.RATING_INSPIRED] = cursor.getInt(COLUMN_INSPIRED);
        String note = cursor.getString(COLUMN_NOTE);
        int id = cursor.getInt(COLUMN_ID);
        boolean isUpdateNeeded = cursor.getInt(COLUMN_NEED_UPDATE) == 1;

        MoodThing thing = new MoodThing(id, timestamp, ratings);
        thing.setNote(note);
        thing.setIsUpdateNeeded(isUpdateNeeded);

        return thing;
    }

    public ArrayList<MoodThing> readAllWithinRange(boolean normalize) {
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM \"" + TABLE_MOOD + "\" " +
                "WHERE (mood<>0 OR accurateMood<>0) AND " +
                "TIMESTAMP BETWEEN " + Global.rangeStart.getTime() / 1000
                + " AND " + Global.rangeEndRequested.getTime() / 1000 + " ORDER BY TIMESTAMP DESC", null);

        ArrayList<MoodThing> loadedEntries = new ArrayList<MoodThing>(cursor.getCount());
        while (cursor.moveToNext()) {
            MoodThing thing = readCursor(cursor);
            loadedEntries.add(thing);
        }

        if (loadedEntries.size() > 0) {
            long minTimestamp = loadedEntries.get(loadedEntries.size() - 1).timestamp;
            long maxTimestamp = loadedEntries.get(0).timestamp;
            int timeStampDiff = (int) (maxTimestamp - minTimestamp);

            Global.rangeStart = new Date((minTimestamp * 1000) - 1000);
            Global.rangeEnd = new Date((maxTimestamp * 1000) + 1000);

            if (normalize) {
                for (MoodThing entry : loadedEntries) {
                    entry.normalizedTimestamp = normalize(entry.timestamp, minTimestamp, timeStampDiff);
                }
            }
        } else {
            Global.rangeEnd = new Date();
        }

        cursor.close();
        close();

        int totalMood = 0;
        for (MoodThing currentEntry : loadedEntries) {
            totalMood += currentEntry.ratings[MoodThing.RATING_MOOD];
        }
        if (loadedEntries.size() == 0){
            MoodThing.averageMood = -1;
        } else {
            MoodThing.averageMood = (int) Math.round((double) totalMood / (double) loadedEntries.size());
        }

        return loadedEntries;
    }

    public List<MoodThing> readAllNeedUpdate() {
        SQLiteDatabase database = getWritableDatabase();
        ArrayList<MoodThing> things = new ArrayList<>();
        Cursor cursor = database.query(TABLE_MOOD, null, COLUMN_NEED_UPDATE_NAME + "=1", null, null, null, null);
        while (cursor.moveToNext()) {
            things.add(readCursor(cursor));
        }

        cursor.close();
        close();

        return things;
    }

    public void setAllUpdated() {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("UPDATE " + TABLE_MOOD + " SET " + COLUMN_NEED_UPDATE_NAME + "=0 WHERE " + COLUMN_NEED_UPDATE_NAME + "=1;");
        close();
    }

    public int count(Date rangeStart, Date rangeEnd){
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM \"" + TABLE_MOOD + "\" WHERE " +
                "TIMESTAMP BETWEEN " + rangeStart.getTime() / 1000 +
                " AND " + rangeEnd.getTime() / 1000 + " ORDER BY TIMESTAMP DESC", null);
        cursor.moveToFirst();
        int count =  cursor.getInt(0);
        cursor.close();
        close();

        return count;
    }

    public ArrayList<MoodThing> readAll(boolean normalize, Date rangeStart, Date rangeEnd,int limit, int offset) {
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM \"" + TABLE_MOOD + "\" WHERE " +
                "TIMESTAMP BETWEEN " + rangeStart.getTime() / 1000 +
                " AND " + rangeEnd.getTime() / 1000 + " ORDER BY TIMESTAMP DESC LIMIT " + String.valueOf(offset) + "," + limit, null);

        ArrayList<MoodThing> loadedEntries = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            loadedEntries.add(readCursor(cursor));
        }

        if (normalize && loadedEntries.size() > 0) {
            long minTimestamp = loadedEntries.get(loadedEntries.size() - 1).timestamp;
            long maxTimestamp = loadedEntries.get(0).timestamp;
            int timeStampDiff = (int) (maxTimestamp - minTimestamp);

            for (MoodThing entry : loadedEntries) {
                entry.normalizedTimestamp = normalize(entry.timestamp, minTimestamp, timeStampDiff);
            }
        }

        cursor.close();
        close();

        return loadedEntries;
    }

    public Uri exportCsv() {
        try {
            String documentsDirPath;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                // We can't use DIRECTORY_DOCUMENTS, so we derive the directory from DIRECTORY_PICTURES instead.
                String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getParent();
                documentsDirPath = rootDir + "/Documents";
            } else {
                documentsDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
            }

            File moodimodoDocumentsDir = new File(documentsDirPath + "/MoodiModo");
            moodimodoDocumentsDir.mkdirs();

            SimpleDateFormat fileDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm");
            fileDateFormat = new SimpleDateFormat(fileDateFormat.toLocalizedPattern());
            String currentTimeString = fileDateFormat.format(new Date());

            File exportFile = new File(moodimodoDocumentsDir, "MoodiModo_export_" + currentTimeString + ".csv");
            if (!exportFile.exists() && !exportFile.createNewFile()) {
                return null;
            }

            SimpleDateFormat moodDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            moodDateFormat = new SimpleDateFormat(moodDateFormat.toLocalizedPattern());

            // Retrieve mood ratings from database
            ArrayList<MoodThing> allMoodThings = readAll(false, new Date(0), new Date(),10000,0);

            // Start with the headers
            StringBuilder builder = new StringBuilder();
            builder.append("Time,Mood,Guiltiness,Alertness,Fear,Excitability,Irritability,Shame,Attentiveness,Hostility,Activeness,Nervousness,Interest,Enthusiasm,Jitteriness,Resilience,Distress,Determination,Upsettedness,Pride,Scaredness,Inspiration,Accurate Mood\n");

            // Now loop through all elements to add the rows
            for (MoodThing currentMoodThing : allMoodThings) {
                builder.append(moodDateFormat.format(new Date(currentMoodThing.timestampMillis)));

                for (int i = 0; i < MoodThing.NUM_RESULT_TYPES; i++) {
                    // If the user inputted this rating
                    if (currentMoodThing.ratings[i] != MoodThing.RATING_VALUE_NULL) {
                        builder.append("," + currentMoodThing.ratings[i]);
                    } else {
                        builder.append(",");
                    }
                }

                builder.setLength(builder.length() - 1);    // Remove trailing comma
                builder.append("\n");                       // Append newline, next row
            }
            Log.d("DatabaseWrapper", "CSV: " + builder.toString());
            // Write the CSV
            PrintWriter out = new PrintWriter(exportFile);
            out.println(builder.toString());
            out.flush();
            out.close();

            // Return the URI so that it can be used elsewhere
            return Uri.fromFile(exportFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static double normalize(double value, final double min, final double maxMinusMin) {
        return 2.0 * ((value - min) / maxMinusMin) - 1.0;
    }

    /***
     * Delets the entire database
     * @return true if the database was successfully deleted, false otherwise
     */
    public boolean dropDatabase(){
        return context.deleteDatabase(DATABASE_NAME);
    }

}
