package utils.input.exception;

public class InvalidLargeInputException extends InvalidInputException{

    public InvalidLargeInputException() {
        super("Provided utils.input is not a valid large utils.input.\n");
    }

    public InvalidLargeInputException(String message) {
        super(message);
    }

}
