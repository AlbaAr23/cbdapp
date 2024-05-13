package com.example.cbdapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "PRODUCTOS";
    public static final String TABLE_NAME_CONFIG = "CONFIG";
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
    public static final String CREATE_TABLE_CONFIG = "create table " + TABLE_NAME_CONFIG + "(" +
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
    public void onCreateTrad(SQLiteDatabase db, String lang) {
       String TABLE_NAME_LANG=TABLE_NAME +"_"+lang;
        String CREATE_TABLE_lang = "create table " + TABLE_NAME_LANG+ "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT NOT NULL, " +
                DESC + " TEXT, " +
                CAT + " TEXT, " +
                IMG + " TEXT );";
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LANG);
        db.execSQL(CREATE_TABLE_lang);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}



