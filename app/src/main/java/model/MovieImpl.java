package model;

public class MovieImpl extends AbstractMovie {

    public MovieImpl(String id, String title, int year, String posterName) {
        super(id, title, year, posterName);
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

    @Override
    public String toString() {
        return id + " " + title + " " + year + " " + posterName;
    }
}
