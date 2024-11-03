package utils.input.exception;

public class InvalidWordException extends InvalidInputException{
    public InvalidWordException(){
        super("Given value is not a valid word");
    }

    public InvalidWordException(String message) {
        super(message);
    }
}
