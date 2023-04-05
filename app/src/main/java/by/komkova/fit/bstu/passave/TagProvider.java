package by.komkova.fit.bstu.passave;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TagProvider extends ContentProvider {
    static final String log_tag = FolderProvider.class.getName();
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    static final String AUTHORITY = "by.komkova.fit.bstu.passave.providers.TagList";
    static final String PATH = "list";
    public static final Uri TAG_URI = Uri.parse("content://" + AUTHORITY + "/"+
            PATH);

    static final String TAG_LIST_TYPE = "vnd.android.cursor.dir/vnd."+
            AUTHORITY+"."+PATH;
    static final String TAG_TYPE = "vnd.android.cursor.item/vnd."+
            AUTHORITY+"."+PATH;
    static final int URI_TAGS = 1;
    static final int URI_TAG_ID = 2;

    private static UriMatcher uriMathcher;

    static{
        uriMathcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMathcher.addURI(AUTHORITY, PATH, URI_TAGS);
        uriMathcher.addURI(AUTHORITY, PATH + "/#", URI_TAG_ID);
    }

    @Override
    public boolean onCreate() {
        Log.d(log_tag, "TagProvider onCreate()");
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMathcher.match(uri)){
            case URI_TAGS:
                Log.d(log_tag, "URI_TAGS");
                if(TextUtils.isEmpty(sortOrder)){
                    sortOrder = DatabaseHelper.TAG_COLUMN_TAG_NAME + " ASC";
                }
                break;
            case URI_TAG_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_TAG_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection = selection + " AND " + DatabaseHelper.TAG_COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TAG_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(),
                TAG_URI);
        Log.d(log_tag, "query completed, "+ uri.toString());

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMathcher.match(uri)){
            case URI_TAG_ID:
                return TAG_TYPE;
            case URI_TAGS:
                return TAG_LIST_TYPE;
        }
        Log.d(log_tag, "getType, "+ uri.toString());
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if(uriMathcher.match(uri) != URI_TAGS){
            throw new IllegalArgumentException("wrong uri: "+uri);
        }
        db = databaseHelper.getWritableDatabase();
        long rowID = db.insert(databaseHelper.TAG_TABLE, null, contentValues);
        Uri result = ContentUris.withAppendedId(TAG_URI, rowID);
        getContext().getContentResolver().notifyChange(result, null);
        Log.d(log_tag, "insert completed, "+ uri.toString());

        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMathcher.match(uri)){
            case URI_TAGS:
                Log.d(log_tag, "URI_TAGS");

                break;
            case URI_TAG_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_TAG_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection =  DatabaseHelper.TAG_COLUMN_ID + " = " + id;
                }
                else{
                    selection = selection + " AND " + DatabaseHelper.TAG_COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();

        int rowCount = db.delete(DatabaseHelper.TAG_TABLE, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(log_tag, "delete completed, "+ uri.toString());

        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMathcher.match(uri)){
            case URI_TAGS:
                Log.d(log_tag, "URI_TAGS");
                break;
            case URI_TAG_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_TAG_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection =  DatabaseHelper.TAG_COLUMN_ID + " = " + id;
                }
                else{
                    selection = selection + " AND " + DatabaseHelper.TAG_COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();

        int rowCount = db.update(DatabaseHelper.TAG_TABLE, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(log_tag, "updated, "+ uri.toString());

        return rowCount;
    }
}
