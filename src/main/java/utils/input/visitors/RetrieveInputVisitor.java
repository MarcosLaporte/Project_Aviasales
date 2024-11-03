package utils.input.visitors;

import utils.input.exception.*;
import utils.input.single_input.StringInput;
import utils.input.single_input.StringInput.TypeOfString;

import java.util.Scanner;

public final class RetrieveInputVisitor implements OperationInputVisitor {

    public RetrieveInputVisitor() {

    }

    public void visitString(StringInput stringInput)
        throws InvalidAddressException,
            InvalidNameException,
            InvalidPhoneNumberException,
            InvalidEmailException,
            InvalidLargeInputException
    {
        Scanner sc = new Scanner(System.in);
        String input = "";
        TypeOfString typeOfString = stringInput.getType();
        switch(typeOfString){
            case NONE -> {
                StringBuilder sb = new StringBuilder();
                while(sc.hasNextLine()){
                    String line = sc.nextLine();
                    if (line.isEmpty()){
                        break;
                    }
                    sb.append(line + "\n");
                }
                input = sb.toString();
            }
            case ADDRESS -> input = validateSingleLineInput(sc, typeOfString,
                    "A valid address should not contain any blank spaces.\n" +
                            "Example: 1234, StreetName, CityName, StateName");
            case NAME -> input = validateSingleLineInput(sc, typeOfString,
                    "A valid name should begin with uppercase letter (A-Z), be at least 3 letters and a maximum of 16.\n" +
                            "Example: Antonio");
            case PHONE -> input = validateSingleLineInput(sc, typeOfString,
                    "A valid phone should be a number containing exactly 10 digits\nExample: 123456789");
            case EMAIL -> input = validateSingleLineInput(sc, typeOfString,
                    "A valid email address should contain a sequence of characters similar to name@domain.com\n");
            case LARGE_INPUT -> input = validateSingleLineInput(sc, typeOfString,
                    "Input is too short");
            case FILE_PATH -> input = validateSingleLineInput(sc, typeOfString,
                    "A path should follow the next format: <Letter>:/folder1/folder2/<more folders>/file.extension");
            case NUMBER -> input = validateSingleLineInput(sc, typeOfString,
                    "A valid number should only contain characters ranging from 0 to 9.");
            case DECIMAL -> input = validateSingleLineInput(sc, typeOfString,
                    "Not a valid decimal number.");
            case WORD -> input = validateSingleLineInput(sc, typeOfString,
                    "A valid word should only contain alphabetic characters.");
            case URL -> input = validateSingleLineInput(sc, typeOfString,
                    "A valid URL may follow the format http[s]://route.to.resource:[port]/");
            case USERNAME -> input = validateSingleLineInput(sc, typeOfString,
                    "Usernames may contain alphabetic characters, numbers, hyphens and underscores (-_)");
            case PASSWORD -> input = validateSingleLineInput(sc, typeOfString,
                    "Passwords may contain alphabetic characters and special characters *_+-=()");
            case DATE -> input = validateSingleLineInput(sc, typeOfString,
                    "Dates should be written in the following format: aaaa-mm-dd");
        };
        stringInput.setValue(input);
    }

    public String validateSingleLineInput(Scanner sc, TypeOfString type, String message){
        String input = sc.nextLine();
        if(!input.matches(type.getRegex())){
            throw new InvalidWordException(message);
        }
        if((type.equals(TypeOfString.DATE) && java.sql.Date.valueOf(input) == null)){
            throw new InvalidWordException("Date is not a valid date");
        }
        return input;
    }
}
