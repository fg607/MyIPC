package com.hardwork.fg607.myipc.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by fg607 on 16-7-12.
 */
public class BookProvider extends ContentProvider {

    private static final String TAG = "BookProvider";

    public static final String AUTHORITY = "com.hardwork.fg607.myipc.provider";

    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/book");

    public static final Uri USER_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/user");

    public static final int BOOK_URI_CODE = 0;

    public static final int USER_URI_CODE = 1;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {

        sUriMatcher.addURI(AUTHORITY,"book",BOOK_URI_CODE);
        sUriMatcher.addURI(AUTHORITY,"user",USER_URI_CODE);

    }

    private Context mContext;
    private SQLiteDatabase mDb;

    private String getTableName(Uri uri){

        String tableName = null;

        switch (sUriMatcher.match(uri)){

            case BOOK_URI_CODE:
                tableName = DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = DbOpenHelper.USER_TABLE_NAME;
                break;
            default:
                break;
        }

        return tableName;
    }
    @Override
    public boolean onCreate() {

        mContext = getContext();

        initProviderData();
        return false;
    }

    private void initProviderData() {

        mDb = new DbOpenHelper(mContext).getWritableDatabase();

        mDb.execSQL("delete from "+DbOpenHelper.BOOK_TABLE_NAME);
        mDb.execSQL("delete from "+DbOpenHelper.USER_TABLE_NAME);

        mDb.execSQL("insert into book values(1,'Android');");
        mDb.execSQL("insert into book values(2,'C++');");
        mDb.execSQL("insert into book values(3,'Python');");
        mDb.execSQL("insert into user values(1,'Tom',1);");
        mDb.execSQL("insert into user values(2,'Jack',1);");
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String tableName = getTableName(uri);

        if(tableName==null){

            throw new IllegalArgumentException("Unsupported URI:"+uri);
        }

        return mDb.query(tableName,projection,selection,selectionArgs,null,null,sortOrder,null);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String tableName = getTableName(uri);

        if(tableName==null){

            throw new IllegalArgumentException("Unsupported URI:"+uri);
        }

        mDb.insert(tableName,null,values);

        mContext.getContentResolver().notifyChange(uri,null);

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = getTableName(uri);

        if(tableName==null){

            throw new IllegalArgumentException("Unsupported URI:"+uri);
        }
        int count = mDb.delete(tableName,selection,selectionArgs);

        if(count>0){

            mContext.getContentResolver().notifyChange(uri,null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        String tableName = getTableName(uri);

        if(tableName==null){

            throw new IllegalArgumentException("Unsupported URI:"+uri);
        }
        int row = mDb.update(tableName,values,selection,selectionArgs);

        if(row>0){

            mContext.getContentResolver().notifyChange(uri,null);
        }
        return row;
    }
}
