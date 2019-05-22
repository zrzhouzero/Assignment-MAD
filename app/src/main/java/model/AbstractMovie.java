package model;

import java.io.Serializable;

public abstract class AbstractMovie implements Movie, Serializable {

    protected String id;
    protected String title;
    protected int year;
    protected String posterName;

    public AbstractMovie(String id, String title, int year, String posterName) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.posterName = posterName;
    }

}
