package by.komkova.fit.bstu.passave.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String MAIN_DB_NAME = "main_db.db";
    private static final int SCHEMA = 1;
    public static final String SETTINGS_TABLE = "settings";
    public static final String FOLDER_TABLE = "folder";
    public static final String PASSWORD_NOTE_TABLE = "password_note";
    public static final String NOTE_TABLE = "note";
    public static final String TAG_TABLE = "tag";
    public static final String SECURITY_TABLE = "security_algorithm";

    // settings table
    public static final String SETTINGS_COLUMN_ID = "_id";
    public static final String SETTINGS_COLUMN_MASTER_KEY = "mk";
    public static final String SETTINGS_COLUMN_CREATED = "created";

    // security table
    public static final String SECURITY_COLUMN_ID = "_id";
    public static final String SECURITY_COLUMN_ALGORITHM_NAME = "algorithm_name";

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
    public static final String PN_COLUMN_KEY = "private_key";

    public DatabaseHelper(Context context) {
        super(context, MAIN_DB_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + FOLDER_TABLE
                + " (" + FOLDER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + FOLDER_COLUMN_TAG_ID + " INTEGER, "
                + FOLDER_COLUMN_UPDATED + " TEXT NOT NULL, "
                + FOLDER_COLUMN_FOLDER_NAME + " TEXT NOT NULL, "
                + "constraint tag_id_fk foreign key(" + FOLDER_COLUMN_TAG_ID + ") references "
                + TAG_TABLE + "(" + TAG_COLUMN_ID +") on delete set null on update cascade);");

        sqLiteDatabase.execSQL("CREATE TABLE " + NOTE_TABLE
                + " (" + NOTE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + NOTE_COLUMN_TAG_ID + " INTEGER, "
                + NOTE_COLUMN_UPDATED + " TEXT NOT NULL, "
                + NOTE_COLUMN_TEXT + " TEXT NOT NULL, "
                + "constraint tag_id_fk foreign key(" + NOTE_COLUMN_TAG_ID + ") references "
                + TAG_TABLE + "(" + TAG_COLUMN_ID +") on delete set null on update cascade);");

        sqLiteDatabase.execSQL("CREATE TABLE " + TAG_TABLE
                + " (" + TAG_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + TAG_COLUMN_UPDATED + " TEXT NOT NULL, "
                + TAG_COLUMN_TAG_NAME + " TEXT NOT NULL UNIQUE);");

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
                + PN_COLUMN_TAG_ID + " INTEGER, "
                + PN_SECURITY_ALGORITHM_ID + " INTEGER, "
                + PN_COLUMN_FAVOURITE + " INTEGER CHECK(" + PN_COLUMN_FAVOURITE + " = 0 OR " + PN_COLUMN_FAVOURITE +" = 1) DEFAULT 0, "
                + PN_COLUMN_CREATED + " TEXT NOT NULL, "
                + PN_COLUMN_UPDATED + " TEXT NOT NULL, "
                + PN_COLUMN_KEY + " TEXT DEFAULT NULL, "
                + "constraint security_id_fk foreign key(" + PN_SECURITY_ALGORITHM_ID + ") references "
                + SECURITY_TABLE + "(" + SECURITY_COLUMN_ID + ") on delete cascade on update cascade, "
                + "constraint tag_id_fk foreign key(" + PN_COLUMN_TAG_ID + ") references "
                + TAG_TABLE + "(" + TAG_COLUMN_ID + ") on delete set null on update cascade, "
                + "constraint folder_id_fk foreign key(" + PN_COLUMN_FOLDER_ID + ") references "
                + FOLDER_TABLE + "(" + FOLDER_COLUMN_ID +") on delete set null on update cascade);");

        sqLiteDatabase.execSQL("CREATE TABLE " + SETTINGS_TABLE
                + " (" + SETTINGS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + SETTINGS_COLUMN_MASTER_KEY + " TEXT NOT NULL, "
                + SETTINGS_COLUMN_CREATED + " TEXT NOT NULL);");

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
        db.execSQL("INSERT INTO " + SECURITY_TABLE +
                " (" + SECURITY_COLUMN_ALGORITHM_NAME+ ") " +
                " VALUES ('AES');");
    }

}
