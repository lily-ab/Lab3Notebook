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

public class SecondFragment extends Fragment {
    ListView tagList;
    DataBaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor tagCursor;
    SimpleCursorAdapter tagAdapter;
    ImageView imageView;

    private Context tagContext;

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
        final View view = inflater.inflate(R.layout.activity_tag_list, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        tagContext = view.getContext();
        imageView=view.findViewById(R.id.imageView3);
        imageView.setImageResource(R.drawable.left);

        tagList=view.findViewById(R.id.tagList);
        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(tagContext, TagActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        databaseHelper = new DataBaseHelper(tagContext);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора
        tagCursor =  db.rawQuery("select * from "+ DataBaseHelper.TAG_TABLE, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DataBaseHelper.TAG_NAME};
        // создаем адаптер, передаем в него курсор
        tagAdapter = new SimpleCursorAdapter(tagContext, android.R.layout.two_line_list_item,
                tagCursor, headers, new int[]{android.R.id.text1}, 0);
        tagList.setAdapter(tagAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        tagCursor.close();
    }

    // по нажатию на кнопку запускаем UserActivity для добавления данных
    public void addTag(View view){
        Intent intent = new Intent(tagContext, TagActivity.class);
        startActivity(intent);
    }
}
