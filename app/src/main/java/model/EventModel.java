package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class EventModel {

    private ArrayList<EventImpl> events;
    private ArrayList<MovieImpl> moviesInLibrary;

    public EventModel() {}

    public void initialisation(File eventsFile, File moviesFile) throws FileNotFoundException, ParseException {
        events = new ArrayList<>();
        moviesInLibrary = new ArrayList<>();
        Scanner eventIn = new Scanner(eventsFile);
        Scanner movieIn = new Scanner(moviesFile);
        while (movieIn.hasNext()) {
            String str = movieIn.nextLine();
            if (str.contains("//")) continue;
            String[] temp = str.replace("\"", "").split(",");
            moviesInLibrary.add(new MovieImpl(temp[0], temp[1], Integer.parseInt(temp[2]), temp[3]));
        }
        while (eventIn.hasNext()) {
            String str = eventIn.nextLine();
            if (str.contains("//")) continue;
            String[] temp = str.replace("\"", "").split(",");
            String datePattern = "dd/MM/yyyy H:mm:ss a";
            SimpleDateFormat format = new SimpleDateFormat(datePattern);

            EventImpl tempEvent = new EventImpl(temp[0], temp[1], format.parse(temp[2].toUpperCase()), format.parse(temp[3].toUpperCase()),
                    temp[4], new Location(Double.parseDouble(temp[5]), Double.parseDouble(temp[6])));

            if (temp.length > 7) {
                if (!temp[7].equals("")) {
                    tempEvent.setMovie(findMovieById(temp[7]));
                }
            }

            if (temp.length > 8) {
                if (!temp[8].equals("")) {
                    String[] tempAttendeesRead = temp[8].split(":");
                    ArrayList<String> tempAttendees = new ArrayList<>(Arrays.asList(tempAttendeesRead));
                    tempEvent.setAttendees(tempAttendees);
                }
            }

            events.add(tempEvent);
        }
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

}
