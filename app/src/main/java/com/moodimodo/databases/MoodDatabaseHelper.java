package com.moodimodo.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.quantimodo.tools.sync.SyncHelper;

import java.util.Map;
import java.util.WeakHashMap;

import static com.moodimodo.databases.DatabaseWrapper.*;

@Deprecated
class MoodDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;

    private WeakHashMap<Thread, Boolean> states = new WeakHashMap<>();
    private Context context;

    public MoodDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createTable(TABLE_MOOD,database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion == 3 && newVersion == 4) {
            String tempTable = TABLE_MOOD + "_migrate";
            createTable(tempTable,database);

            String sql = "ALTER TABLE " + TABLE_MOOD +
                    " ADD COLUMN " + COLUMN_NOTE_NAME + " TEXT;";
            database.execSQL(sql);

            sql = "ALTER TABLE " + TABLE_MOOD + " ADD COLUMN " + COLUMN_NEED_UPDATE_NAME + " INTEGER;";
            database.execSQL(sql);

            sql = "INSERT INTO " + tempTable + " select * FROM " + TABLE_MOOD + ";";
            database.execSQL(sql);

            sql = "DROP TABLE " + TABLE_MOOD + ";";
            database.execSQL(sql);

            sql = "ALTER TABLE " + tempTable + " RENAME TO " + TABLE_MOOD + ";";
            database.execSQL(sql);

            sql = "UPDATE " + TABLE_MOOD + " SET " + COLUMN_NOTE_NAME + "='', " + COLUMN_NEED_UPDATE_NAME + "=0;";
            database.execSQL(sql);
        }
        else if(oldVersion == 4 && newVersion == 5){
            String tempTable = TABLE_MOOD + "_migrate";
            createTable(tempTable,database);

            String sql = "ALTER TABLE " + TABLE_MOOD +
                    " ADD COLUMN " + COLUMN_ID_NAME + " TEXT;";
            database.execSQL(sql);

            sql = "INSERT INTO " + tempTable + " select * FROM " + TABLE_MOOD + ";";
            database.execSQL(sql);

            sql = "DROP TABLE " + TABLE_MOOD + ";";
            database.execSQL(sql);

            sql = "ALTER TABLE " + tempTable + " RENAME TO " + TABLE_MOOD + ";";
            database.execSQL(sql);
            // we set the new column as zero but also marked as to need update to refresh the data
            sql = "UPDATE " + TABLE_MOOD + " SET " + COLUMN_ID_NAME + "=0, " + COLUMN_NEED_UPDATE_NAME + "=0;";
            database.execSQL(sql);

            SyncHelper.invokeFullSync(context, false);

        }

    }

    public void createTable(String tableName, SQLiteDatabase database) {
        database.execSQL("CREATE TABLE "
                + tableName
                + " ("
                + COLUMN_TIMESTAMP_NAME + " INTEGER PRIMARY KEY NOT NULL, "
                + COLUMN_MOOD_NAME + " INTEGER, "
                + COLUMN_MOOD_ACCURATE_NAME + " INTEGER, "
                + COLUMN_GUILTY_NAME + " INTEGER, "
                + COLUMN_ALERT_NAME + " INTEGER, "
                + COLUMN_AFRAID_NAME + " INTEGER, "
                + COLUMN_EXCITED_NAME + " INTEGER, "
                + COLUMN_IRRITABLE_NAME + " INTEGER, "
                + COLUMN_ASHAMED_NAME + " INTEGER, "
                + COLUMN_ATTENTIVE_NAME + " INTEGER, "
                + COLUMN_HOSTILE_NAME + " INTEGER, "
                + COLUMN_ACTIVE_NAME + " INTEGER, "
                + COLUMN_NERVOUS_NAME + " INTEGER, "
                + COLUMN_INTERESTED_NAME + " INTEGER, "
                + COLUMN_ENTHUSIASTIC_NAME + " INTEGER, "
                + COLUMN_JITTERY_NAME + " INTEGER, "
                + COLUMN_STRONG_NAME + " INTEGER, "
                + COLUMN_DISTRESSED_NAME + " INTEGER, "
                + COLUMN_DETERMINED_NAME + " INTEGER, "
                + COLUMN_UPSET_NAME + " INTEGER, "
                + COLUMN_PROUD_NAME + " INTEGER, "
                + COLUMN_SCARED_NAME + " INTEGER, "
                + COLUMN_INSPIRED_NAME + " INTEGER, "
                + COLUMN_NOTE_NAME + " TEXT, "
                + COLUMN_NEED_UPDATE_NAME + " INTEGER, "
                + COLUMN_ID_NAME + " INTEGER"
                + ");");
    }


    public static void requestSync(Context context) {
        SyncHelper.invokeSync(context);
    }

    public SQLiteDatabase getWritableDatabase() {
        synchronized(this) {
            Thread currentThread = Thread.currentThread();
            states.put(currentThread, true);
            return super.getWritableDatabase();
        }
    }

    /**
     * Close database if all threads dont need the database anymore
     * @return true if closed, false otherwise
     */
    public boolean closeIfNeeded() {
        // synchronized because it may be accessible by multi threads (all dbHelper methods are synchronized)
        // and synchronized on the object because open/close are related on each other
        synchronized(this) {
            Thread currentThread = Thread.currentThread();

            states.put(currentThread, false); // this thread requires that this database should be closed

            boolean mustBeClosed = true;

            // if all threads asked for closing database, then close it
            Boolean opened = null;
            Thread thread = null;
            for (Map.Entry<Thread, Boolean> entry : states.entrySet()) {
                thread = entry.getKey();
                opened = entry.getValue();
                if (thread != null && opened != null) {
                    if (opened.booleanValue()) {
                        // one thread still requires that database should be opened
                        mustBeClosed = false;
                    }
                }
            }

            if (mustBeClosed) {
                super.close();
            }

            return mustBeClosed;
        }
    }
}