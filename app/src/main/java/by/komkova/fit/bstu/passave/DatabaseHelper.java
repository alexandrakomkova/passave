package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    // private static final String SETTINGS_DB_NAME = "settings.db";
    private static final String MAIN_DB_NAME = "main_db.db";
    private static final int SCHEMA = 1;
    static final String SETTINGS_TABLE = "settings";
    static final String FOLDER_TABLE = "folder";
    static final String PASSWORD_NOTE_TABLE = "password_note";

    // settings db
    public static final String SETTINGS_COLUMN_THEME = "_id";
    public static final String SETTINGS_COLUMN_LANGUAGE = "name";
    public static final String SETTINGS_COLUMN_MASTERKEY = "year";
    // public static final String SETTINGS_COLUMN_CREATION_DATE = "year";

    // folder table
    public static final String FOLDER_COLUMN_ID = "_id";
    public static final String FOLDER_COLUMN_FOLDER_NAME = "folder_name";
    public static final String FOLDER_COLUMN_UPDATED = "updated";

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

    public DatabaseHelper(Context context) {
        // super(context, SETTINGS_DB_NAME, null, SCHEMA);
        super(context, MAIN_DB_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + FOLDER_TABLE
                + " (" + FOLDER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + FOLDER_COLUMN_UPDATED + " TEXT NOT NULL, "
                + FOLDER_COLUMN_FOLDER_NAME + " TEXT UNIQUE NOT NULL);");

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

//        "CREATE TABLE \"password_note\" (\n" +
//                "\t\"_id\"\tINTEGER NOT NULL,\n" +
//                "\t\"service_name\"\tTEXT NOT NULL,\n" +
//                "\t\"folder_id\"\tINTEGER,\n" +
//                "\t\"login\"\tTEXT NOT NULL,\n" +
//                "\t\"password\"\tTEXT NOT NULL,\n" +
//                "\t\"description\"\tTEXT,\n" +
//                "\t\"favourite\"\tINTEGER CHECK(\"favourite\" = 0 OR \"favourite\" = 1),\n" +
//                "\tFOREIGN KEY(\"folder_id\") REFERENCES \"folder\"(\"_id\") on delete set null on update cascade,\n" +
//                "\tPRIMARY KEY(\"_id\" AUTOINCREMENT)\n" +
//                ");"
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FOLDER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PASSWORD_NOTE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public Cursor readAllFoldersData()
    {
        String query = "select * from " + FOLDER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readAllPasswordNotesData()
    {
        String query = "select * from " + PASSWORD_NOTE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

}
