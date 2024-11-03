package utils.input.exception;

public class InvalidNameException extends InvalidInputException{

    public InvalidNameException() {
        super("Provided utils.input is not a valid name\n");
    }

    public InvalidNameException(String message) {
        super(message);
    }
}
