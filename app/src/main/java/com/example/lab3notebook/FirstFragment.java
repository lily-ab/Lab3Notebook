package com.example.lab3notebook;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {
    private Context context;
    ListView noteList;
    DataBaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor noteCursor;
    ImageView imageView;
    /** Handle the results from the voice recognition activity. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.activity_note_list, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();
        //выводим текст который хотим

        noteList = view.findViewById(R.id.list);
        imageView=view.findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.right);
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note=globalNotes.get(position);
                Intent intent = new Intent(getActivity(), NoteActivity.class);
                intent.putExtra("id", note.get_Id());
                startActivity(intent);
            }
        });

        databaseHelper = new DataBaseHelper(getActivity());
        return view;
    }

    List<Note> globalNotes=new ArrayList<>();
    @Override
    public void onResume() {
        super.onResume();
        // открываем подключение
        db = databaseHelper.getReadableDatabase();
        List<Note> notes=new ArrayList<>();
        //получаем данные из бд в виде курсора
        noteCursor =  db.rawQuery("select * from "+ DataBaseHelper.NOTE_TABLE, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DataBaseHelper.NOTE_TITLE, DataBaseHelper.NOTE_DATE, DataBaseHelper.NOTE_TEXT};

        if (noteCursor.moveToFirst()) {
            do {
                Note note = new Note(noteCursor.getLong(noteCursor.getColumnIndex(DataBaseHelper.NOTE_ID)),
                        noteCursor.getString(noteCursor.getColumnIndex(DataBaseHelper.NOTE_TITLE)),
                        noteCursor.getString(noteCursor.getColumnIndex(DataBaseHelper.NOTE_DATE)),
                        noteCursor.getString(noteCursor.getColumnIndex(DataBaseHelper.NOTE_TEXT)));

                notes.add(note);
            } while (noteCursor.moveToNext());
            globalNotes=notes;
        }

        List<NoteTag> tags=new ArrayList<>();
        //получаем данные из бд в виде курсора
        noteCursor =  db.rawQuery("select * from "+ DataBaseHelper.NOTE_TAG_TABLE, null);

        if (noteCursor.moveToFirst()) {
            do {
                NoteTag tag = new NoteTag(noteCursor.getInt(noteCursor.getColumnIndex(DataBaseHelper.NOTE_TAG_ID)),
                        noteCursor.getInt(noteCursor.getColumnIndex(DataBaseHelper._NOTE_ID)),
                        noteCursor.getInt(noteCursor.getColumnIndex(DataBaseHelper._TAG_ID)));

                tags.add(tag);
            } while (noteCursor.moveToNext());
            globalNotes=notes;
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


       // notes.add(new Note(headers[0],headers[1],headers[2]));
        noteList.setAdapter(new CustomListAdapter(context,notes));
        // создаем адаптер, передаем в него курсор
//        noteAdapter = new SimpleCursorAdapter(context, android.R.layout.two_line_list_item,
//                noteCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
//        noteList.setAdapter(noteAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        noteCursor.close();
    }

    // по нажатию на кнопку запускаем UserActivity для добавления данных
    public void addNote(View view){
        Intent intent = new Intent(context, NoteActivity.class);
        startActivity(intent);
    }

    public void tags(View view) {
        Intent intent = new Intent(context, TagListActivity.class);
        startActivity(intent);
    }
}
