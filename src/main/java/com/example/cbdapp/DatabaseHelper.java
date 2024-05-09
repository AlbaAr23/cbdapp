package com.example.cbdapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "PRODUCTOS";

    // Table columns


    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String DESC = "description";
    public static final String CAT = "category";
    public static final String IMG = "image";
    // Database Information
    static final String DB_NAME = "PRODUCTOS.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT NOT NULL, " +
            DESC + " TEXT, " +
            CAT + " TEXT, " +
            IMG + " TEXT );";
    public static final String CREATE_TABLE_CONFIG = "create table " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT NOT NULL, " +
            DESC + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}



