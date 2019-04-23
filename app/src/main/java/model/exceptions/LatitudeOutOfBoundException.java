package model.exceptions;

public class LatitudeOutOfBoundException extends Exception {

    @Override
    public String getMessage() {
        return "Latitude out of bound.";
    }
}
