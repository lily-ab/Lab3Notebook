package com.example.lab3notebook;

import java.util.List;

public class Note {
    private long id;
    private String title;
    private String text;
    private String date;

    private List<Tag> tags;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Note(long id, String title, String date, String text){
        super();
        this.id =id;
        this.title=title;
        this.text=text;
        this.date=date;
    }

    public long get_Id() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
