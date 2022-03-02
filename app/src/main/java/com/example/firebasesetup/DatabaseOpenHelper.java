package com.example.firebasesetup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static String DATABASE_NAME = "Taxi_Times_DB.db";
    private static int DATABASE_VERSION = 2;
    private final File DB_FILE;

    public DatabaseOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DB_FILE = context.getDatabasePath(DATABASE_NAME);
    }
}
