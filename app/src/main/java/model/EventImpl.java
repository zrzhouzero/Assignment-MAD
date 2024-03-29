package model;

import java.util.ArrayList;
import java.util.Date;

public class EventImpl extends AbstractEvent {

    public EventImpl(String id, String title, Date startDate, Date endDate, String venue, MyLocation myLocation) {
        super(id, title, startDate, endDate, venue, myLocation);
    }

    public EventImpl(String id, String title, Date startDate, Date endDate, String venue, MyLocation myLocation, MovieImpl movie, ArrayList<String> attendees) {
        super(id, title, startDate, endDate, venue, myLocation, movie, attendees);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventImpl)) return false;
        EventImpl e = (EventImpl) obj;
        return e.getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id.equals("") ? 0 : id.hashCode();
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
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public String getVenue() {
        return venue;
    }

    @Override
    public MyLocation getMyLocation() {
        return myLocation;
    }

    @Override
    public MovieImpl getMovie() {
        return movie;
    }

    @Override
    public ArrayList<String> getAttendees() {
        return attendees;
    }

    @Override
    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    @Override
    public void removeAttendees(final String attendeeName) {
        this.attendees.removeIf(e -> e.equals(attendeeName));
    }

    @Override
    public void setMovie(MovieImpl movie) {
        this.movie = movie;
    }

    @Override
    public String toString() {
        if (movie != null) {
            return id + " " + title + " " + movie.getTitle() + " " + attendees.size();
        } else {
            return id + " " + title + " null " + attendees.size();
        }
    }
}
