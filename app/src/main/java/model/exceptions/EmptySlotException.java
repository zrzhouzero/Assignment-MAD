package model.exceptions;

public class EmptySlotException extends Exception {

    @Override
    public String getMessage() {
        return "Empty slot";
    }

}
