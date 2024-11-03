package utils.input.exception;

public class InvalidPhoneNumberException extends InvalidInputException {

    public InvalidPhoneNumberException() {
        super("Provided utils.input is not a valid phone number.");
    }

    public InvalidPhoneNumberException(String message) {
        super(message);
    }
}
