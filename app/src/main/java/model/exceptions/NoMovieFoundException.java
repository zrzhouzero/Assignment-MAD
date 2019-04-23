package model.exceptions;

public class NoMovieFoundException extends Exception {

    @Override
    public String getMessage() {
        return "No movies found.";
    }
}
