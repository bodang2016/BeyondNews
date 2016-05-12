package com.example.boyu.beyondnews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.ByteArrayOutputStream;

/**
 * Created by boyu on 2016/3/21.
 */
public class LocalDatabaseHelper extends SQLiteOpenHelper {

    final String CREATE_TABLE_SQL = "create table if not exists localDatabase_info(_id integer primary key,"
//            + "newsid integer,"
            + "type integer,"
            + "title varchar,"
            + "desc varchar,"
            + "image varchar,"
            + "date date," +
            "accountid integer," +
            "accountname varchar," +
            "favtype integer," +
            "accountcomm varchar)";

    public LocalDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("--------onUpdate has been called--------"
                + oldVersion + "---->" + newVersion);
    }

    public long insertBmp(byte[] img) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("image", img);
        long result = db.insert("localDatabase", null, cv);
        return result;
    }

    public Bitmap getBmp(int position)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from localDatabase",null);
        cursor.moveToPosition(position);
        byte[] in = cursor.getBlob(cursor.getColumnIndex("img"));
        Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
        return bmpout;
    }

}
