package model;

import java.util.ArrayList;
import java.util.Date;

public interface Event {

    String getId();

    String getTitle();

    Date getStartDate();

    Date getEndDate();

    String getVenue();

    Location getLocation();

    MovieImpl getMovie();

    ArrayList<String> getAttendees();

    void setAttendees(ArrayList<String> attendees);

    void removeAttendees(String AttendeeName);

    void setMovie(MovieImpl movie);

}
