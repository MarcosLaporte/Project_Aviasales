package utils.input.exception;

public class InvalidAddressException extends InvalidInputException{

    public InvalidAddressException() {
        super("Provided utils.input is not a valid address\n");
    }

    public InvalidAddressException(String message) {
        super(message);
    }
}
