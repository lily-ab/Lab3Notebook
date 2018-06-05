package com.example.lab3notebook;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity {
    EditText titleBox;
    EditText textBox;
    Button delButton;
    Button saveButton;
    TextView date;
    ListView tagListView;
    private Context tagContext;

    DataBaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor noteCursor;
    long noteId =0;
    Cursor tagCursor;
    SimpleCursorAdapter tagAdapter;
    List<Tag> newTags=new ArrayList<>();
    List<Tag> tags=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        titleBox = findViewById(R.id.title);
        textBox = findViewById(R.id.text);
        delButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);
        date=findViewById(R.id.date);
        tagListView=findViewById(R.id.tagNoteList);

        sqlHelper = new DataBaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            noteId = extras.getLong("id");
        }
        // если 0, то добавление
        List<Tag> tagList = new ArrayList<>();
        if (noteId > 0) {
            // получаем элемент по id из бд
            noteCursor = db.rawQuery("select * from " + DataBaseHelper.NOTE_TABLE + " where " +
                    DataBaseHelper.NOTE_ID + "=?", new String[]{String.valueOf(noteId)});
            noteCursor.moveToFirst();
            titleBox.setText(noteCursor.getString(1));
            textBox.setText(String.valueOf(noteCursor.getString(2)));
            noteCursor.close();

            Cursor cursor = db.rawQuery("select "+DataBaseHelper.TAG_TABLE+".* from " + DataBaseHelper.TAG_TABLE + " inner join "+
                    DataBaseHelper.NOTE_TAG_TABLE+" on "+DataBaseHelper.NOTE_TAG_TABLE+"."+DataBaseHelper._TAG_ID+"="+DataBaseHelper.TAG_TABLE+"."+DataBaseHelper.TAG_ID +
                    " where "+DataBaseHelper.NOTE_TAG_TABLE+"."+DataBaseHelper._NOTE_ID+"="+noteId, null);
            if (cursor.moveToFirst()) {
                do {
                    Tag tag = new Tag(cursor.getLong(cursor.getColumnIndex(DataBaseHelper.TAG_ID)),
                            cursor.getString(cursor.getColumnIndex(DataBaseHelper.TAG_NAME)));

                    tagList.add(tag);
                } while (cursor.moveToNext());
            }
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
        }
        tagList.size();

        tagCursor =  db.rawQuery("select * from "+ DataBaseHelper.TAG_TABLE, null);
        if (tagCursor.moveToFirst()) {
            do {
                Tag tag = new Tag(tagCursor.getLong(tagCursor.getColumnIndex(DataBaseHelper.TAG_ID)),
                        tagCursor.getString(tagCursor.getColumnIndex(DataBaseHelper.TAG_NAME)));

                tags.add(tag);
            } while (tagCursor.moveToNext());
        }
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DataBaseHelper.TAG_NAME};
        // создаем адаптер, передаем в него курсор
        tagAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice,
                tagCursor, headers, new int[]{android.R.id.text1}, 0);
        tagListView.setAdapter(tagAdapter);

        // добвляем для списка слушатель
        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                SparseBooleanArray sp=tagListView.getCheckedItemPositions();

                List<Tag> selectedItems=new ArrayList<>();
                for(int i=0;i < tags.size();i++)
                {
                    if(sp.get(i))
                        selectedItems.add(tags.get(i));
                }
                newTags=selectedItems;
            }
        });
    }

    public void save(View view){
        if(titleBox.getText().length()<=0|| titleBox.getText().toString()=="" || textBox.getText().toString()=="" ||
                textBox.getText().length()<=0 || textBox.getText()==null || titleBox.getText()==null){
            Toast toast=Toast.makeText(this, "Пустая строка!",Toast.LENGTH_LONG);
            toast.show();
        }
        else{
            ContentValues cv = new ContentValues();
            cv.put(DataBaseHelper.NOTE_TITLE, titleBox.getText().toString());
            cv.put(DataBaseHelper.NOTE_TEXT, textBox.getText().toString());

            db.delete(DataBaseHelper.NOTE_TAG_TABLE,DataBaseHelper._NOTE_ID+"= ?",new String[] {String.valueOf(noteId)});

            if (noteId > 0) {
                db.update(DataBaseHelper.NOTE_TABLE, cv, DataBaseHelper.NOTE_ID + "=" + String.valueOf(noteId), null);
                if(newTags!=null)
                    for(Tag tag:newTags){
                        db.execSQL("insert into "+DataBaseHelper.NOTE_TAG_TABLE+"("+DataBaseHelper._NOTE_ID+", "+
                                DataBaseHelper._TAG_ID+") values ("+noteId+","+ tag.getId() +");");
                    }

                // db.execSQL("INSERT INTO "+NOTE_TAG_TABLE+"("+_TAG_ID+", "+ _NOTE_ID +") VALUES (1,1);");
                newTags=null;

            } else {
                db.insert(DataBaseHelper.NOTE_TABLE, null, cv);
                long newId=0;
                Cursor c=db.rawQuery("select "+DataBaseHelper.NOTE_ID+" from "+DataBaseHelper.NOTE_TABLE+" order by "+DataBaseHelper.NOTE_ID+" DESC limit 1", null);
                if (c.moveToFirst()) {
                    newId = c.getLong(c.getColumnIndex(DataBaseHelper.NOTE_ID));

                    if (newTags != null)
                        for (Tag tag : newTags) {
                            db.execSQL("insert into " + DataBaseHelper.NOTE_TAG_TABLE + "(" + DataBaseHelper._NOTE_ID + ", " +
                                    DataBaseHelper._TAG_ID + ") values (" + newId + "," + tag.getId() + ");");
                        }
                }

                // db.execSQL("INSERT INTO "+NOTE_TAG_TABLE+"("+_TAG_ID+", "+ _NOTE_ID +") VALUES (1,1);");
                newTags=null;
            }
            goHome();
        }

    }

    public void delete(View view){
        db.delete(DataBaseHelper.NOTE_TABLE, "_id = ?", new String[]{String.valueOf(noteId)});
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
