package com.example.firebasedemo1setup.Data;

public class NoteFormat
{
    private String noteTitle = "";
    private String noteDescription = "";
    private String noteID = "";
    private String noteDate = "";
    private String imageUrl = "";

    public NoteFormat() {
    }

    public NoteFormat(String noteTitle, String noteDescription, String noteID, String noteDate, String imageUrl)
    {
        this.noteTitle = noteTitle;
        this.noteDescription = noteDescription;
        this.noteID = noteID;
        this.noteDate = noteDate;
        this.imageUrl = imageUrl;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteDescription() {
        return noteDescription;
    }

    public void setNoteDescription(String noteDescription) {
        this.noteDescription = noteDescription;
    }

    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
