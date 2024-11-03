package utils.input.exception;

import java.util.InputMismatchException;

public class InvalidInputException extends InputMismatchException {
    public InvalidInputException() {
        super("The data entered doesn't match with the expected data");;
    }

    public InvalidInputException(String message) {
        super(message);
    }
}
