package com.example.boyu.beyondnews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bodang on 5/3/16.
 */
public class ReviewDatabaseHelper extends SQLiteOpenHelper{

    final String CREATE_TABLE_SQL = "create table if not exists reviewDatabase_info(_id integer primary key,"
//            + "newsid integer,"
            + "newsid integer,"
            + "user varchar,"
            + "content varchar,"
            + "date date)";

    public ReviewDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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
}
