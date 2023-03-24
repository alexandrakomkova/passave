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

public class PasswordNoteProvider extends ContentProvider {

    static final String log_tag = PasswordNoteProvider.class.getName();
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    static final String AUTHORITY = "by.komkova.fit.bstu.providers.PasswordNoteList";
    static final String PATH = "list";
    public static final Uri PASSWORD_NOTE_URI = Uri.parse("content://" + AUTHORITY + "/"+
            PATH);

    static final String PASSWORD_NOTE_LIST_TYPE = "vnd.android.cursor.dir/vnd."+
            AUTHORITY+"."+PATH;
    static final String PASSWORD_NOTE_TYPE = "vnd.android.cursor.item/vnd."+
            AUTHORITY+"."+PATH;
    static final int URI_PASSWORD_NOTES = 1;
    static final int URI_PASSWORD_NOTE_ID = 2;

    private static UriMatcher uriMathcher;

    static{
        uriMathcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMathcher.addURI(AUTHORITY, PATH, URI_PASSWORD_NOTES);
        uriMathcher.addURI(AUTHORITY, PATH + "/#", URI_PASSWORD_NOTE_ID);
    }

    @Override
    public boolean onCreate() {
        Log.d(log_tag, "PasswordNoteProvider onCreate");
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMathcher.match(uri)){
            case URI_PASSWORD_NOTES:
                Log.d(log_tag, "URI_PASSWORD_NOTES");
                if(TextUtils.isEmpty(sortOrder)){
                    sortOrder = databaseHelper.PN_COLUMN_SERVICE_NAME + " ASC";
                }
                break;
            case URI_PASSWORD_NOTE_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_PASSWORD_NOTE_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection = selection + " AND " +databaseHelper.PN_COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(databaseHelper.PASSWORD_NOTE_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(),
                PASSWORD_NOTE_URI);
        Log.d(log_tag, "query completed, "+ uri.toString());

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMathcher.match(uri)){
            case URI_PASSWORD_NOTE_ID:
                return PASSWORD_NOTE_TYPE;
            case URI_PASSWORD_NOTES:
                return PASSWORD_NOTE_LIST_TYPE;
        }
        Log.d(log_tag, "getType, "+ uri.toString());
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if(uriMathcher.match(uri) != URI_PASSWORD_NOTES){
            throw new IllegalArgumentException("wrong uri: "+uri);
        }
        db = databaseHelper.getWritableDatabase();
        long rowID = db.insert(databaseHelper.PASSWORD_NOTE_TABLE, null, contentValues);
        Uri result = ContentUris.withAppendedId(PASSWORD_NOTE_URI, rowID);
        getContext().getContentResolver().notifyChange(result, null);
        Log.d(log_tag, "insert completed, "+ uri.toString());

        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMathcher.match(uri)){
            case URI_PASSWORD_NOTES:
                Log.d(log_tag, "URI_PASSWORD_NOTES");

                break;
            case URI_PASSWORD_NOTE_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_PASSWORD_NOTE_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection =  databaseHelper.PN_COLUMN_ID + " = " + id;
                }
                else{
                    selection = selection + " AND " +databaseHelper.PN_COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();

        int rowCount = db.delete(databaseHelper.PASSWORD_NOTE_TABLE, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(log_tag, "delete completed, "+ uri.toString());

        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMathcher.match(uri)){
            case URI_PASSWORD_NOTES:
                Log.d(log_tag, "URI_PASSWORD_NOTES");
                break;
            case URI_PASSWORD_NOTE_ID:
                String id = uri.getLastPathSegment();
                Log.d(log_tag, "URI_PASSWORD_NOTE_ID = "+ id);
                if(TextUtils.isEmpty(selection)){
                    selection =  databaseHelper.PN_COLUMN_ID + " = " + id;
                }
                else{
                    selection = selection + " AND " +databaseHelper.PN_COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("wrong URI: " + uri);
        }
        db = databaseHelper.getWritableDatabase();

        int rowCount = db.update(databaseHelper.PASSWORD_NOTE_TABLE, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(log_tag, "updated, "+ uri.toString());

        return rowCount;
    }
}
