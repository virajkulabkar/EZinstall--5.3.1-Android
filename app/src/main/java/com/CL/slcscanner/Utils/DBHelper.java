package com.CL.slcscanner.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.CL.slcscanner.Pojo.ListResponse.List;
import com.CL.slcscanner.Pojo.ListResponse.ListResponse;
import com.CL.slcscanner.Pojo.PoleMaster.Datum;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBSLX.db";
    public static final String SLC_TABLE_NAME = "SLCIData";

    //field details of SLCImport
    public static final String SLC_COLUMN_SLC_ID = "SlcId";
    public static final String SLC_COLUMN_POLE_ID = "PoleId";
    public static final String SLC_COLUMN_MAC_ID = "MacId";
    public static final String SLC_COLUMN_ID = "ID";
    public static final String SLC_LAT = "Lattitude";
    public static final String SLC_LONG = "Longgitude";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table " + SLC_TABLE_NAME +
                        "(" + SLC_COLUMN_ID + " text," +
                        SLC_COLUMN_SLC_ID + " text," +
                        SLC_COLUMN_POLE_ID + " text," +
                        SLC_COLUMN_MAC_ID + " text," +
                        SLC_LAT + " text," +
                        SLC_LONG + " text)"

        );

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + SLC_TABLE_NAME);
        onCreate(database);
    }

    public boolean insertSLCData(String ID, String slcId, String macId, String poleId, String lat, String longgitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SLC_COLUMN_ID, ID);
        contentValues.put(SLC_COLUMN_SLC_ID, slcId);
        contentValues.put(SLC_COLUMN_POLE_ID, poleId);

        contentValues.put(SLC_COLUMN_MAC_ID, macId);
        contentValues.put(SLC_LAT, lat);
        contentValues.put(SLC_LONG, longgitude);

        db.insertWithOnConflict(SLC_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        // db.execSQL("INSERT INTO "+BRIDGE_TABLE_NAME+"("+BRIDGE_NAME+","+BRIDGE_ADDRESS+") VALUES("+bName+","+bAddress+")");
        //db.rawQuery("INSERT INTO "+BRIDGE_TABLE_NAME+"("+BRIDGE_NAME+","+BRIDGE_ADDRESS+") VALUES("+bName+","+bAddress+")");
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + SLC_TABLE_NAME + " where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SLC_TABLE_NAME);
        return numRows;
    }

    public void deleteTableData(String tblName) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(tblName, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<List> getAllSLC() {
        ArrayList<List> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + SLC_TABLE_NAME, null);

        res.moveToFirst();
        while (res.isAfterLast() == false) {
            List objSlcsBean = new List();
            objSlcsBean.setPoleId(res.getString(res.getColumnIndex(SLC_COLUMN_POLE_ID)));
            objSlcsBean.setSlcId(res.getString(res.getColumnIndex(SLC_COLUMN_SLC_ID)));
            objSlcsBean.setMacAddress(res.getString(res.getColumnIndex(SLC_COLUMN_MAC_ID)));
            objSlcsBean.setID(res.getString(res.getColumnIndex(SLC_COLUMN_ID)));
            objSlcsBean.setLat(res.getString(res.getColumnIndex(SLC_LAT)));
            objSlcsBean.setLng(res.getString(res.getColumnIndex(SLC_LONG)));
            if (!res.getString(res.getColumnIndex(SLC_LAT)).contains("0.0") && !res.getString(res.getColumnIndex(SLC_LONG)).contains("0.0"))
                array_list.add(objSlcsBean);

            res.moveToNext();
        }
        return array_list;
    }
}