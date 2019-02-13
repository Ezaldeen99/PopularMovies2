package com.example.android.popularmovies.DataPackage;

/**
 * Created by azozs on 6/17/2018.
 */

public class MoviesObject {

    private String[] title;
    private String[] year;
    private String[] desc;
    private String[] image;
    private String[] vote;
    private String[] image_id;

    public String[] getImage_id() {
        return image_id;
    }

    public void setImage_id(String[] image_id) {
        this.image_id = image_id;
    }

    public MoviesObject(String[] title, String[] year, String[] desc, String[] image, String[] vote, String[] image_id) {
        this.title = title;
        this.year = year;
        this.desc = desc;
        this.image = image;
        this.vote = vote;
        this.image_id = image_id;
    }

    public String[] getVote() {
        return vote;
    }

    public void setVote(String[] vote) {
        this.vote = vote;
    }

    public void setDesc(String[] desc) {
        this.desc = desc;
    }

    public void setImage(String[] image) {
        this.image = image;
    }

    public void setYear(String[] year) {
        this.year = year;
    }

    public void setTitle(String[] title) {
        this.title = title;
    }

    public String[] getDesc() {
        return desc;
    }

    public String[] getImage() {
        return image;
    }

    public String[] getTitle() {
        return title;
    }

    public String[] getYear() {
        return year;
    }

}
