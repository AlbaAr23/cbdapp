package com.example.cbdapp;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DBManager {

    private static final String COMMA_DELIMITER = ";";
    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String name, String desc, String cat, String img) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TITLE, name);
        contentValue.put(DatabaseHelper.DESC, desc);
        contentValue.put(DatabaseHelper.CAT, cat);
        contentValue.put(DatabaseHelper.IMG, img);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        //DatabaseHelper.DESC, DatabaseHelper.CAT,DatabaseHelper.IMG
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.TITLE,DatabaseHelper.DESC, DatabaseHelper.CAT ,DatabaseHelper.IMG };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor fetchCategorias() {
        //DatabaseHelper.DESC, DatabaseHelper.CAT,DatabaseHelper.IMG
        String[] columns = new String[] {  DatabaseHelper.CAT  };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, DatabaseHelper.CAT, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor fetchProducto(String id) {
        Log.d("ADebugTag", "Value: id =" + id);

        String where = DatabaseHelper._ID + " = "+id;
        Log.d("ADebugTag", "Value: " + id);
        String[] whereArgs = { id };
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.TITLE,DatabaseHelper.DESC, DatabaseHelper.CAT ,DatabaseHelper.IMG };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns,where , null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }else{
            Log.d("ADebugTag", "sin cursor : " + id);
        }
        Log.d("ADebugTag", "Value: despues" + cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID)));

        return cursor;
    }
    public Cursor fetchProductoCategoria(String cat) {
        String where = DatabaseHelper.CAT + " LIKE ?";
        Log.d("ADebugTag", "Value: " + cat);
        String[] whereArgs = { cat };
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.TITLE,DatabaseHelper.DESC, DatabaseHelper.CAT ,DatabaseHelper.IMG };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns,where , whereArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public String getall(){
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.TITLE, DatabaseHelper.DESC, DatabaseHelper.CAT,DatabaseHelper.IMG };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return "null";
    }
    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TITLE, name);
        contentValues.put(DatabaseHelper.DESC, desc);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }
    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }
    public void fillDatabase(Activity context,InputStream file) throws IOException {
        BufferedReader reader = null;
            reader = new BufferedReader(
                    new InputStreamReader(file));

            // do reading, usually loop until end of file reading
            String mLine;
        while ((mLine = reader.readLine())!=null) {
            List<String> datos=  this.getRecordFromLine( mLine);
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.TITLE, datos.get(0));
            contentValue.put(DatabaseHelper.DESC, datos.get(1));
            contentValue.put(DatabaseHelper.CAT, datos.get(2));
            contentValue.put(DatabaseHelper.IMG, datos.get(3));
            
            database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);

            }

                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }


    }
    public void drop(){
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_NAME);
        database.execSQL(DatabaseHelper.CREATE_TABLE);
    }
    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

}
