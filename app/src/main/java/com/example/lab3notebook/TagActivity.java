package com.example.lab3notebook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TagActivity extends AppCompatActivity {
    EditText tagNameBox;
    Button delButton;
    Button saveButton;

    DataBaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor noteCursor;
    ListView noteList;
    long tagId =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        tagNameBox = findViewById(R.id.name);
        delButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);
        noteList=findViewById(R.id.noteTagList);

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        sqlHelper = new DataBaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tagId = extras.getLong("id");
        }

        if (tagId > 0) {
            // получаем элемент по id из бд
            noteCursor = db.rawQuery("select * from " + DataBaseHelper.TAG_TABLE + " where " +
                    DataBaseHelper.TAG_ID + "=?", new String[]{String.valueOf(tagId)});
            noteCursor.moveToFirst();
            tagNameBox.setText(noteCursor.getString(1));
            noteCursor.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
        }

        List<Note> notes=new ArrayList<>();
        noteCursor =  db.rawQuery("select * from "+ DataBaseHelper.NOTE_TABLE+" inner join "+DataBaseHelper.NOTE_TAG_TABLE
                +" on "+DataBaseHelper.NOTE_TABLE+"."+DataBaseHelper.NOTE_ID+"="+DataBaseHelper._NOTE_ID+" where "+DataBaseHelper._TAG_ID+"="+tagId, null);
        if (noteCursor.moveToFirst()) {
            do {
                Note note = new Note(noteCursor.getLong(noteCursor.getColumnIndex(DataBaseHelper.NOTE_ID)),
                        noteCursor.getString(noteCursor.getColumnIndex(DataBaseHelper.NOTE_TITLE)),
                        noteCursor.getString(noteCursor.getColumnIndex(DataBaseHelper.NOTE_DATE)),
                        noteCursor.getString(noteCursor.getColumnIndex(DataBaseHelper.NOTE_TEXT)));

                notes.add(note);
            } while (noteCursor.moveToNext());
        }
        for(Note note:notes) {
            List<Tag> tagList = new ArrayList<>();
            Cursor cursor = db.rawQuery("select "+DataBaseHelper.TAG_TABLE+".* from " + DataBaseHelper.TAG_TABLE + " inner join "+
                    DataBaseHelper.NOTE_TAG_TABLE+" on "+DataBaseHelper.NOTE_TAG_TABLE+"."+DataBaseHelper._TAG_ID+"="+DataBaseHelper.TAG_TABLE+"."+DataBaseHelper.TAG_ID +
                    " where "+DataBaseHelper.NOTE_TAG_TABLE+"."+DataBaseHelper._NOTE_ID+"="+note.getId(), null);
            if (cursor.moveToFirst()) {
                do {
                    Tag tag = new Tag(cursor.getLong(cursor.getColumnIndex(DataBaseHelper.TAG_ID)),
                            cursor.getString(cursor.getColumnIndex(DataBaseHelper.TAG_NAME)));

                    tagList.add(tag);
                } while (cursor.moveToNext());
            }
            note.setTags(tagList);
        }
        if(notes.size()!=0)
            noteList.setAdapter(new CustomListAdapter(this,notes));
    }

    public void saveTag(View view){
        if(tagNameBox.getText().length()<=0 || tagNameBox.getText()==null || tagNameBox.getText().toString()==""){
            Toast toast=Toast.makeText(this, "Пустая строка!",Toast.LENGTH_LONG);
            toast.show();
        }
        else{
            ContentValues cv = new ContentValues();
            cv.put(DataBaseHelper.TAG_NAME, tagNameBox.getText().toString());
            if (tagId > 0) {
                db.update(DataBaseHelper.TAG_TABLE, cv, DataBaseHelper.TAG_ID + "=" + String.valueOf(tagId), null);
            } else {
                db.insert(DataBaseHelper.TAG_TABLE, null, cv);
            }
            goHome();
        }
    }

    public void deleteTag(View view){
        db.delete(DataBaseHelper.TAG_TABLE, "_id = ?", new String[]{String.valueOf(tagId)});
        goHome();
    }

    private void goHome(){
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
