package com.example.qfind;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataHandler extends SQLiteOpenHelper {

    private static final String db = "myDb";
    private static final int version = 1;


    public DataHandler(Context context) {
        super(context, db, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_PATH_TABLE = "CREATE TABLE FILE_PATHS (id INTEGER PRIMARY KEY AUTOINCREMENT, PATH TEXT)";
        sqLiteDatabase.execSQL(CREATE_PATH_TABLE);

    }


    public void insertData(String path, SQLiteDatabase database) {

        ContentValues values = new ContentValues();
        values.put("PATH", path);

        database.insert("FILE_PATHS", null, values);


    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //DROP THE TABLE IF EXIST ALREADY
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "FILE_PATHS");
        //CREATE A NEW TABLE
        onCreate(sqLiteDatabase);


    }

    public void clearDatabase(SQLiteDatabase db,String TABLE_NAME) {
        String clearDBQuery = "DELETE FROM FILE_PATHS";
        db.execSQL(clearDBQuery);
    }

}