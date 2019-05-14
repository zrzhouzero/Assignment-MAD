package model;

import java.io.Serializable;

public class MovieImpl implements Movie, Serializable {

    private String id;
    private String title;
    private int year;
    private String posterName;

    public MovieImpl(String id, String title, int year, String posterName) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.posterName = posterName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public String getPosterName() {
        return posterName;
    }

}
