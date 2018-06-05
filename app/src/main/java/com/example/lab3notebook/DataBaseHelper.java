package com.example.lab3notebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "noteDB.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String NOTE_TABLE = "note"; // название таблицы в бд
    // названия столбцов
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TITLE = "title";
    public static final String NOTE_TEXT = "text";
    public static final String NOTE_DATE = "date";

    static final String TAG_TABLE = "tag";
    public static final String TAG_ID="_id";
    public static final String TAG_NAME = "name";

    static final String NOTE_TAG_TABLE = "note_tag";
    public static final String NOTE_TAG_ID="_id";
    public static final String _TAG_ID="_id_tag";
    public static final String _NOTE_ID="_id_note";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS note ( " +
                NOTE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOTE_TITLE +" TEXT, "+
                NOTE_TEXT +" TEXT ," +
                NOTE_DATE +" DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')));");
        // добавление начальных данных
        db.execSQL("INSERT INTO "+ NOTE_TABLE +" (" + NOTE_TITLE
                + ", " + NOTE_TEXT + ") VALUES ('Пойти спать', 'Не забыть пойти спать')," +
                "('Проснуться','Попытаться проснуться');");

        db.execSQL("CREATE TABLE IF NOT EXISTS tag ( " +
                TAG_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TAG_NAME +" TEXT);");
        db.execSQL("INSERT INTO "+TAG_TABLE+"("+TAG_NAME+") VALUES ('Сон')");

        db.execSQL("CREATE TABLE IF NOT EXISTS note_tag ( " +
                NOTE_TAG_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                _TAG_ID +" INTEGER, " +
                _NOTE_ID+" INTEGER);");
        db.execSQL("INSERT INTO "+NOTE_TAG_TABLE+"("+_TAG_ID+", "+ _NOTE_ID +") VALUES (1,1);");


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+NOTE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+TAG_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+NOTE_TAG_TABLE);
        onCreate(db);
    }

    public void addNote(Note note){
        //for logging
        //Log.d("addNote", note.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(NOTE_TITLE, note.getTitle());
        values.put(NOTE_TEXT, note.getText());
        values.put(NOTE_DATE, note.getDate().toString());

        // 3. insert
        db.insert(NOTE_TABLE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }
}
