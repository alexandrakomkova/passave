package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String SETTINGS_DB_NAME = "settings.db";
    private static final String MAIN_DB_NAME = "main_db.db";
    private static final int SCHEMA = 1;
    static final String SETTINGS_TABLE = "settings";
    static final String FOLDER_TABLE = "folder";
    static final String PASSWORD_NOTE_TABLE = "password_note";

    // settings db
    public static final String SETTINGS_COLUMN_THEME = "_id";
    public static final String SETTINGS_COLUMN_LANGUAGE = "name";
    public static final String SETTINGS_COLUMN_MASTERKEY = "year";
    public static final String SETTINGS_COLUMN_CREATION_DATE = "year";

    // folder table
    public static final String FOLDER_COLUMN_ID = "_id";
    public static final String FOLDER_COLUMN_FOLDER_NAME = "folder_name";
    public static final String FOLDER_COLUMN_UPDATED = "updated";

    // password notes table
    public static final String PN_COLUMN_ID = "_id";
    public static final String PN_COLUMN_SERVICE_NAME = "service_name";
    public static final String PN_COLUMN_LOGIN = "login";
    public static final String PN_COLUMN_PASSWORD = "password";
    public static final String PN_COLUMN_CREATED = "created";
    public static final String PN_COLUMN_UPDATED = "updated";
    public static final String PN_COLUMN_DESCRIPTION = "description";
    public static final String PN_COLUMN_FOLDER_ID = "folder_id";
    public static final String PN_COLUMN_FAVOURITE = "favourite";

    public DatabaseHelper(Context context) {
        // super(context, SETTINGS_DB_NAME, null, SCHEMA);
        super(context, MAIN_DB_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FOLDER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PASSWORD_NOTE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
