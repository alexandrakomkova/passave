package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String MAIN_DB_NAME = "main_db.db";
    private static final int SCHEMA = 1;
    static final String SETTINGS_TABLE = "settings";
    static final String FOLDER_TABLE = "folder";
    static final String PASSWORD_NOTE_TABLE = "password_note";
    static final String NOTE_TABLE = "note";
    static final String TAG_TABLE = "tag";
    static final String SECURITY_TABLE = "security_algorithm";

    // settings table
    public static final String SETTINGS_COLUMN_THEME = "theme";
    public static final String SETTINGS_COLUMN_LANGUAGE = "language";
    public static final String SETTINGS_COLUMN_MASTER_KEY = "mk";
    public static final String SETTINGS_COLUMN_CREATION_DATE = "created";
    public static final String SETTINGS_COLUMN_UPDATED_DATE = "updated";

    // security table
    public static final String SECURITY_COLUMN_ID = "_id";
    public static final String SECURITY_COLUMN_ALGORITHM_NAME = "folder_name";

    // folder table
    public static final String FOLDER_COLUMN_ID = "_id";
    public static final String FOLDER_COLUMN_FOLDER_NAME = "folder_name";
    public static final String FOLDER_COLUMN_UPDATED = "updated";
    public static final String FOLDER_COLUMN_TAG_ID = "tag_id";

    // tag table
    public static final String TAG_COLUMN_ID = "_id";
    public static final String TAG_COLUMN_TAG_NAME = "tag_name";
    public static final String TAG_COLUMN_UPDATED = "updated";

    // note table
    public static final String NOTE_COLUMN_ID = "_id";
    public static final String NOTE_COLUMN_TEXT = "note_text";
    public static final String NOTE_COLUMN_UPDATED = "updated";
    public static final String NOTE_COLUMN_TAG_ID = "tag_id";

    // password notes table
    public static final String PN_COLUMN_ID = "_id";
    public static final String PN_COLUMN_SERVICE_NAME = "service_name";
    public static final String PN_COLUMN_LOGIN = "login";
    public static final String PN_COLUMN_PASSWORD = "password";
    public static final String PN_COLUMN_DESCRIPTION = "description";
    public static final String PN_COLUMN_FOLDER_ID = "folder_id";
    public static final String PN_COLUMN_FAVOURITE = "favourite";
    public static final String PN_COLUMN_CREATED = "created";
    public static final String PN_COLUMN_UPDATED = "updated";
    public static final String PN_COLUMN_TAG_ID = "tag_id";
    public static final String PN_SECURITY_ALGORITHM_ID = "security_algorithm_id";

    public DatabaseHelper(Context context) {
        super(context, MAIN_DB_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + FOLDER_TABLE
                + " (" + FOLDER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + FOLDER_COLUMN_UPDATED + " TEXT NOT NULL, "
                + FOLDER_COLUMN_FOLDER_NAME + " TEXT UNIQUE NOT NULL);");

        sqLiteDatabase.execSQL("CREATE TABLE " + NOTE_TABLE
                + " (" + NOTE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + NOTE_COLUMN_UPDATED + " TEXT NOT NULL, "
                + NOTE_COLUMN_TEXT + " TEXT NOT NULL);");

        sqLiteDatabase.execSQL("CREATE TABLE " + TAG_TABLE
                + " (" + TAG_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + TAG_COLUMN_UPDATED + " TEXT NOT NULL, "
                + TAG_COLUMN_TAG_NAME + " TEXT NOT NULL);");

        sqLiteDatabase.execSQL("CREATE TABLE " + SECURITY_TABLE
                + " (" + SECURITY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + SECURITY_COLUMN_ALGORITHM_NAME + " TEXT NOT NULL);");

        sqLiteDatabase.execSQL("CREATE TABLE " + PASSWORD_NOTE_TABLE
                + " (" + PN_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + PN_COLUMN_SERVICE_NAME + " TEXT NOT NULL, "
                + PN_COLUMN_LOGIN + " TEXT NOT NULL, "
                + PN_COLUMN_PASSWORD + " TEXT NOT NULL, "
                + PN_COLUMN_DESCRIPTION + " TEXT, "
                + PN_COLUMN_FOLDER_ID + " INTEGER, "
                + PN_COLUMN_FAVOURITE + " INTEGER CHECK(" + PN_COLUMN_FAVOURITE + " = 0 OR " + PN_COLUMN_FAVOURITE +" = 1) DEFAULT 0, "
                + PN_COLUMN_CREATED + " TEXT NOT NULL, "
                + PN_COLUMN_UPDATED + " TEXT NOT NULL, "
                + " constraint folder_id_fk foreign key(" + PN_COLUMN_FOLDER_ID + ") references " + FOLDER_TABLE + "(" + FOLDER_COLUMN_ID +")"
                + " on delete set null on update cascade);");

        insertFoldersToDatabase(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FOLDER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PASSWORD_NOTE_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SECURITY_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void insertFoldersToDatabase(SQLiteDatabase db) {

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        db.execSQL("INSERT INTO " + FOLDER_TABLE +
                " (" + FOLDER_COLUMN_FOLDER_NAME + ", " + FOLDER_COLUMN_UPDATED+ ") " +
                " VALUES ('No folder', \'"+ df.format(currentDate) + "\');");

        db.execSQL("INSERT INTO " + FOLDER_TABLE +
                " (" + FOLDER_COLUMN_FOLDER_NAME + ", " + FOLDER_COLUMN_UPDATED+ ") " +
                " VALUES ('Favourite', \'"+ df.format(currentDate) + "\');");
    }

}
