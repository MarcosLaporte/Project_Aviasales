package utils.input.exception;

public class InvalidEmailException extends InvalidInputException {
    public InvalidEmailException() {
        super("Provided utils.input is not a valid email");
    }

    public InvalidEmailException(String message) {
        super(message);
    }
}
