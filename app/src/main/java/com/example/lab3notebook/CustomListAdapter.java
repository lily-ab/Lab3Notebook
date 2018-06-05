package com.example.lab3notebook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private List<Note> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapter(Context aContext,  List<Note> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();
            holder.titleView = convertView.findViewById(R.id.name);
            holder.textView = convertView.findViewById(R.id.noteText);
            holder.imageView=convertView.findViewById(R.id.imageView);
            holder.date=convertView.findViewById(R.id.date);
            holder.tags=convertView.findViewById(R.id.tagNoteList);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = this.listData.get(position);
        holder.titleView.setText(note.getTitle());
        holder.textView.setText(note.getText());
        holder.imageView.setImageResource(R.drawable.icon);
        holder.date.setText(note.getDate());
        holder.tags.setText(" ");
        List<Tag> tags=new ArrayList<>();
        tags=note.getTags();
        if(tags!=null)
        if(tags.size()!=0){
            for(Tag tag:tags) {
                if(tag!=null)
                    holder.tags.append(tag.getName()+" ");
            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView titleView;
        TextView textView;
        TextView date;
        ImageView imageView;
        TextView tags;
    }
}
