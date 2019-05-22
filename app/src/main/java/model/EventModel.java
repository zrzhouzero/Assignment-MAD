package model;

import java.util.ArrayList;
import java.util.Date;

public class EventModel {

    private ArrayList<EventImpl> events;
    private ArrayList<MovieImpl> moviesInLibrary;

    public EventModel(ArrayList<MovieImpl> movies, ArrayList<EventImpl> events) {
        this.moviesInLibrary = movies;
        this.events = events;
    }

    public void setEvents(ArrayList<EventImpl> events) {
        this.events = events;
    }

    public ArrayList<EventImpl> getEvents() {
        return events;
    }

    public ArrayList<MovieImpl> getMoviesInLibrary() {
        return moviesInLibrary;
    }

    public void removeEventById(String eventId) {
        events.removeIf(e -> e.getId().equals(eventId));
    }

    private MovieImpl findMovieById(String movieId) {
        for (int i = 0; i < moviesInLibrary.size(); i++) {
            if (moviesInLibrary.get(i).getTitle().equals(movieId)) {
                return moviesInLibrary.get(i);
            }
        }
        return null;
    }

    public ArrayList<EventImpl> getSoonest3Events() {
        ArrayList<EventImpl> copiedEvents = new ArrayList<>(this.events);
        copiedEvents.removeIf(e -> e.getStartDate().compareTo(new Date()) < 0);

        ArrayList<EventImpl> result = new ArrayList<>();
        copiedEvents.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
        if (copiedEvents.size() < 3) {
            return copiedEvents;
        } else {
            for (int i = 0; i < 3; i++) {
                result.add(copiedEvents.get(i));
            }
            return result;
        }
    }

}
