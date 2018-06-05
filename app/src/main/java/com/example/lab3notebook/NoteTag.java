package com.example.lab3notebook;

public class NoteTag {
    private int id;
    private int idNote;
    private int idTag;
    public NoteTag(int id,int idNote, int idTag){
        this.id=id;
        this.idNote=idNote;
        this.idTag=idTag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdNote() {
        return idNote;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }

    public int getidTag() {
        return idTag;
    }

    public void setTag(int tag) {
        idTag = tag;
    }
}
