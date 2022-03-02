package com.example.firebasesetup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class DatabaseAccess {
    private SQLiteAssetHelper openHandler;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;
    public static final String TAXITIME_TABLE_NAME;
    public static final String TAXITIME_COLUMN_IATA;
    public static final String TAXITIME_COLUMN_APT_NAME;
    public static final String TAXITIME_COLUMN_MEAN;
    public static final String TAXITIME_COLUMN_STD_DEV;

    static {
        TAXITIME_COLUMN_APT_NAME = "AirportName";
        TAXITIME_TABLE_NAME = "Taxi_times";
        TAXITIME_COLUMN_IATA = "IATA";
        TAXITIME_COLUMN_MEAN = "MeanTXO";
        TAXITIME_COLUMN_STD_DEV = "StandardDeviation";
    }

    //private constructor so that object creation from outside the class is avoided
    public DatabaseAccess(Context context){
        this.openHandler=new DatabaseOpenHelper(context);
    }

    //to return the single instance of database
    public static DatabaseAccess getInstance(Context context){
        if (instance== null)
        {
            instance=new DatabaseAccess(context);
        }
        return instance;
    }

    //opening the database
    public void open(){
        this.openHandler.getWritableDatabase();
    }

    //closing the database connection
    public void close(){
        if (db != null){
            this.db.close();
        }
    }

    //we will query for mean time
    public String getMeanTime(String name){
        String meanT = "";
        open();
        SQLiteDatabase db = this.openHandler.getReadableDatabase();
        Cursor meanTime =  db.rawQuery( "SELECT '" + TAXITIME_COLUMN_MEAN + "' FROM '" + TAXITIME_TABLE_NAME + "' WHERE '" + TAXITIME_COLUMN_IATA + "' = " + "'"+ name + "'",
                null );
        if (meanTime != null)
        {
            meanTime.moveToFirst();
        }
        return meanT;
    }

    public void updateMeanTime(String meanTime, String iataName){
        try (SQLiteDatabase db = this.openHandler.getReadableDatabase()) {
            db.rawQuery("UPDATE '" + TAXITIME_TABLE_NAME + "' SET '" + TAXITIME_COLUMN_MEAN + "' = " + "'" + meanTime +"'" + " WHERE '" + TAXITIME_COLUMN_IATA + "' = '" + iataName + "'", null);
        }
        close();
    }

    public boolean insertContact (String iataName, String aptName, String mean, String stdDev) {
        //open();
        SQLiteDatabase db = this.openHandler.getWritableDatabase();
/*        ContentValues contentValues = new ContentValues();
        contentValues.put("'" + TAXITIME_COLUMN_IATA + "'", iataName);
        contentValues.put("'" + TAXITIME_COLUMN_APT_NAME + "'", aptName);
        contentValues.put("'" + TAXITIME_COLUMN_MEAN + "'", mean);
        contentValues.put("'" + TAXITIME_COLUMN_STD_DEV + "'", stdDev);*/
        String ROW1 = "INSERT INTO " + TAXITIME_TABLE_NAME + " ('" + TAXITIME_COLUMN_IATA + "', '" + TAXITIME_COLUMN_APT_NAME + "', '" + TAXITIME_COLUMN_MEAN + "', '" + TAXITIME_COLUMN_STD_DEV + "') " +
                "VALUES ('" +iataName+"', '" + aptName + "', " + mean + ", '" + stdDev + "')";
        db.execSQL(ROW1);
        close();
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.openHandler.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.openHandler.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TAXITIME_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (String iataName, String aptName, String mean, String stdDev) {
        SQLiteDatabase db = this.openHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAXITIME_COLUMN_IATA, iataName);
        contentValues.put(TAXITIME_COLUMN_APT_NAME, aptName);
        contentValues.put(TAXITIME_COLUMN_MEAN, mean);
        contentValues.put(TAXITIME_COLUMN_STD_DEV, stdDev);
        db.update(TAXITIME_TABLE_NAME, contentValues, "id = ? ", new String[] { iataName } );
        close();
        return true;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.openHandler.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllContacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.openHandler.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(TAXITIME_COLUMN_IATA)));
            res.moveToNext();
        }
        return array_list;
    }
}
