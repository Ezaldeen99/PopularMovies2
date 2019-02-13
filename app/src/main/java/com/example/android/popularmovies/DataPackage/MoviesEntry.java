package com.example.android.popularmovies.DataPackage;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by azozs on 7/22/2018.
 */

@Entity(tableName = "movies")
public class MoviesEntry implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String year;
    private String desc;
    private String image;
    private String vote;
    private String movie_id;


    @Ignore
    public MoviesEntry(String title, String year, String desc, String image, String vote, String movie_id) {
        this.title = title;
        this.year = year;
        this.desc = desc;
        this.image = image;
        this.vote = vote;
        this.movie_id = movie_id;
    }

    public MoviesEntry(int id, String title, String year, String desc, String image, String vote, String movie_id) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.desc = desc;
        this.image = image;
        this.vote = vote;
        this.movie_id = movie_id;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getVote() {
        return vote;
    }
}
