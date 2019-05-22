package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public abstract class AbstractEvent implements Serializable, Event {

    protected String id;
    protected String title;
    protected Date startDate;
    protected Date endDate;
    protected String venue;
    protected MyLocation myLocation;
    protected ArrayList<String> attendees;
    protected MovieImpl movie;

    public AbstractEvent(String id, String title, Date startDate, Date endDate, String venue, MyLocation myLocation) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.venue = venue;
        this.myLocation = myLocation;
        this.attendees = new ArrayList<>();
    }

    public AbstractEvent(String id, String title, Date startDate, Date endDate, String venue, MyLocation myLocation, MovieImpl movie, ArrayList<String> attendees) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.venue = venue;
        this.myLocation = myLocation;
        this.movie = movie;
        this.attendees = attendees;
    }

}
