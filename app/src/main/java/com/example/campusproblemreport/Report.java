package com.example.campusproblemreport;

public class Report {
    private String Description;
    private String Type;
    private String Imageurl;
    private String Location;

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    private String Note;


    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }


    public Report() {
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }
}
