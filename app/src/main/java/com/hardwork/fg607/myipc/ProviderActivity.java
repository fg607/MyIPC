package com.hardwork.fg607.myipc;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ProviderActivity extends AppCompatActivity {

    private final static String TAG = "ProviderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri bookUri = Uri.parse("content://com.hardwork.fg607.myipc.provider/book");

        ContentValues contentValues = new ContentValues();

        contentValues.put("_id",6);
        contentValues.put("name","艺术探索");

        getContentResolver().insert(bookUri,contentValues);

        Cursor bookCursor = getContentResolver().query(bookUri,new String[]{"_id","name"},null,null,null);

        while (bookCursor.moveToNext()){

            int id = bookCursor.getInt(0);

            String name = bookCursor.getString(1);

            Log.i(TAG,name+id);

        }

        bookCursor.close();

    }
}
