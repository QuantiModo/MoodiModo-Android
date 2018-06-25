package com.moodimodo.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.quantimodo.tools.models.*;
import com.quantimodo.tools.sync.SyncHelper;

public class OpenHelper extends DaoMaster.OpenHelper {
    private Context mCtx;

    public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
        mCtx = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1005 && newVersion == 1006){
            String sql = "DROP INDEX pk;" +
                    "DELETE FROM MEASUREMENT WHERE VARIABLE_ID IS NULL;" +
                    "ALTER TABLE MEASUREMENT ADD VARIABLE_NAME TEXT NULL;" +
                    "ALTER TABLE MEASUREMENT ADD UNIT_NAME TEXT NULL;" +
                    "UPDATE MEASUREMENT SET VARIABLE_NAME=(SELECT NAME FROM VARIABLE AS t2 WHERE t2.ID=VARIABLE_ID);" +
                    "UPDATE MEASUREMENT SET UNIT_NAME=(SELECT ABBR FROM UNIT AS t2 WHERE t2.ID=UNIT_ID);" +
                    "DELETE FROM MEASUREMENT WHERE VARIABLE_NAME IS NULL;" +
                    "DELETE FROM MEASUREMENT WHERE UNIT_NAME IS NULL;" +
                    "CREATE TABLE MEASUREMENT4460 " +
                    "(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TIMESTAMP INTEGER NOT NULL," +
                    "VARIABLE_ID INTEGER," +
                    "UNIT_ID INTEGER," +
                    "VALUE REAL NOT NULL," +
                    "SOURCE TEXT," +
                    "NEED_UPDATE INTEGER," +
                    "NOTE TEXT," +
                    "VARIABLE_NAME TEXT  NOT NULL," +
                    "UNIT_NAME TEXT  NOT NULL" +
                    ");" +
                    "INSERT INTO MEASUREMENT4460(TIMESTAMP, VARIABLE_ID, UNIT_ID, VALUE, SOURCE, NEED_UPDATE, NOTE, VARIABLE_NAME, UNIT_NAME) SELECT TIMESTAMP, VARIABLE_ID, UNIT_ID, VALUE, SOURCE, NEED_UPDATE, NOTE, VARIABLE_NAME, UNIT_NAME FROM MEASUREMENT;" +
                    "DROP TABLE MEASUREMENT;" +
                    "ALTER TABLE MEASUREMENT4460 RENAME TO MEASUREMENT;" +
                    "CREATE UNIQUE INDEX pk ON MEASUREMENT(TIMESTAMP,VARIABLE_NAME);";

            String[] commands = sql.split(";");

            try {
                for (String c : commands) {
                    db.execSQL(c + ";");
                }
            } catch (Exception ex){
                DaoMaster.dropAllTables(db, true);
                onCreate(db);
                SyncHelper.invokeFullSync(mCtx, false);
            }

        } else {
            DaoMaster.dropAllTables(db, true);
            onCreate(db);
            SyncHelper.invokeFullSync(mCtx, false);
        }
    }
}
