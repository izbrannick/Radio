package com.example.android.uamp.model;

/**
 * Created by luther on 12/03/2016.
 */
public class Item {

    private String id;
    private String artist;
    private String title;
    private String album;
    private String year;
    private String picture;
    private String buycd;
    private String date_played;
    private String duration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getBuycd() {
        return buycd;
    }

    public void setBuycd(String buycd) {
        this.buycd = buycd;
    }

    public String getDate_played() {
        return date_played;
    }

    public void setDate_played(String date_played) {
        this.date_played = date_played;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return artist + ": " + title + "\n" + album + "-" + year
                + "\n" + duration;
    }
}

