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

public class NoteProvider extends ContentProvider {

    static final String log_tag = NoteProvider.class.getName();
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    static final String AUTHORITY = "by.komkova.fit.bstu.passave.providers.NoteList";
    static final String PATH = "list";
    public static final Uri NOTE_URI = Uri.parse("content://" + AUTHORITY + "/"+
            PATH);

    static final String NOTE_LIST_TYPE = "vnd.android.cursor.dir/vnd."+
            AUTHORITY+"."+PATH;
    static final String NOTE_TYPE = "vnd.android.cursor.item/vnd."+
            AUTHORITY+"."+PATH;
    static final int URI_NOTES = 1;
    static final int URI_NOTE_ID = 2;

    private static UriMatcher uriMathcher;

    static{
        uriMathcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMathcher.addURI(AUTHORITY, PATH, URI_NOTES);
        uriMathcher.addURI(AUTHORITY, PATH + "/#", URI_NOTE_ID);
    }
    @Override
    public boolean onCreate() {
        Log.d(log_tag, "NoteProvider onCreate()");
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMathcher.match(uri)){
            case URI_NOTES:
                Log.d(log_tag, "URI_NOTES");
                if(TextUtils.isEmpty(sortOrder)){
                    sortOrder = databaseHelper.NOTE_COLUMN_TEXT + " ASC";
                }
                break;
            case URI_NOTE_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_NOTE_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection = selection + " AND " +databaseHelper.NOTE_COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(databaseHelper.NOTE_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(),
                NOTE_URI);
        Log.d(log_tag, "query completed, "+ uri.toString());

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMathcher.match(uri)){
            case URI_NOTE_ID:
                return NOTE_TYPE;
            case URI_NOTES:
                return NOTE_LIST_TYPE;
        }
        Log.d(log_tag, "getType, "+ uri.toString());
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if(uriMathcher.match(uri) != URI_NOTES){
            throw new IllegalArgumentException("wrong uri: "+uri);
        }
        db = databaseHelper.getWritableDatabase();
        long rowID = db.insert(databaseHelper.NOTE_TABLE, null, contentValues);
        Uri result = ContentUris.withAppendedId(NOTE_URI, rowID);
        getContext().getContentResolver().notifyChange(result, null);
        Log.d(log_tag, "insert completed, "+ uri.toString());

        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMathcher.match(uri)){
            case URI_NOTES:
                Log.d(log_tag, "URI_NOTES");

                break;
            case URI_NOTE_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_NOTE_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection =  databaseHelper.NOTE_COLUMN_ID + " = " + id;
                }
                else{
                    selection = selection + " AND " +databaseHelper.NOTE_COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();

        int rowCount = db.delete(databaseHelper.NOTE_TABLE, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(log_tag, "delete completed, "+ uri.toString());

        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMathcher.match(uri)){
            case URI_NOTES:
                Log.d(log_tag, "URI_NOTES");
                break;
            case URI_NOTE_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_NOTE_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection =  databaseHelper.NOTE_COLUMN_ID + " = " + id;
                }
                else{
                    selection = selection + " AND " +databaseHelper.NOTE_COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();

        int rowCount = db.update(databaseHelper.NOTE_TABLE, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(log_tag, "updated, "+ uri.toString());

        return rowCount;
    }
}
