package model.exceptions;

public class LongitudeOutOfBoundException extends Exception {

    @Override
    public String getMessage() {
        return "Longitude out of bound.";
    }
}
