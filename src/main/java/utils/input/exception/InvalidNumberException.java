package utils.input.exception;

public class InvalidNumberException extends InvalidInputException{
    public InvalidNumberException(){
        super("Given utils.input is not a valid number");
    }

    public InvalidNumberException(String message) {
        super(message);
    }
}
