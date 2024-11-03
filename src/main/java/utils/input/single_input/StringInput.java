package utils.input.single_input;

import utils.input.SingleInput;
import utils.input.exception.InvalidInputException;
import utils.input.visitors.GetValueVisitor;
import utils.input.visitors.OperationInputVisitor;
import utils.input.visitors.RetrieveInputVisitor;
import utils.input.visitors.ValueInputVisitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class StringInput extends SingleInput<String> {
    public enum TypeOfString {
        ADDRESS("\\d{1,5},[a-zA-Z ]{4,15},[a-zA-Z ]{4,25},[a-zA-Z ]{3,25}"),
        NAME("([A-Z])([a-zA-Z ]{2,15})"),
        EMAIL("\\S+@\\S+\\.\\S+"),
        PHONE("[0-9]{10}"),
        LARGE_INPUT("[\\S\\s]{3,}"),
        FILE_PATH("\"{0,1}[A-Z]:([/\\\\][a-zA-Z0-9.,_-]{2,30})+.[a-zA-Z]{2,15}\"{0,1}"),
        USERNAME("[a-zA-Z0-9-_]{4,15}"),
        PASSWORD("[a-zA-Z0-9*_+-=()]{4,30}"),
        URL("(http[s]{0,1}://){0,1}([a-zA-Z]{2,25}[.]{0,1}){0,}[a-zA-Z]{2,25}:[0-9]{1,5}/"),
        NONE("[\\s\\S]+"),
        NUMBER("[0-9]+"),
        DECIMAL("([0-9]+).([0-9]+)"),
        WORD("[a-zA-Z]+"),
        DATE("([0-9]{4})-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2]\\d)|3[0-1])$"),
        DATETIME("(([0-9]{4})-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2]\\d)|3[0-1]))[T](([0-1][0-9])|([2][0-3])):([0-5][0-9]):([0-5][0-9]))$");

        private String regex;
        private TypeOfString(String regex) {
            this.regex = regex;
        }

        public String getRegex(){
            return regex;
        }
    }

    private TypeOfString type;
    private String currentValue;

    protected Logger logger = LogManager.getLogger(StringInput.class);

    public StringInput() {
        super();
        type = TypeOfString.NONE;
    }

    public StringInput(String name, String displayName, TypeOfString type) {
        super(name, displayName);
        this.type = type;
    }

    public void accept(OperationInputVisitor visitor){
        visitor.visitString(this);
    }

    public String accept(ValueInputVisitor visitor){
        return visitor.visitString(this);
    }

    public TypeOfString getType() {
        return type;
    }

    public void setType(TypeOfString type) {
        this.type = type;
    }
    
    public StringInput processInput(){
        boolean inputIsCorrect = false;
        String inputDisplayName = this.getDisplayName();

        while (!inputIsCorrect) {
            System.out.print( inputDisplayName + ": ");
            try {
                this.accept(new RetrieveInputVisitor());
            } catch (InvalidInputException e) {
                //Main.logger.error(e);
                System.out.println(e.getMessage());
                System.out.println("Please utils.input a valid value.\n");
                continue;
            }

            inputIsCorrect = true;
            this.currentValue = this.accept(new GetValueVisitor());
        }

        return this;
    }

    public Integer asInteger() throws NumberFormatException {
        return Integer.parseInt(this.currentValue);
    }

    public LocalDate asDate() throws DateTimeParseException {
        return LocalDate.parse(this.currentValue);
    }

    public LocalDateTime asDateTime() throws DateTimeParseException {
        return LocalDateTime.parse(this.currentValue);
    }

    public File asFile(){
        if (!this.currentValue.matches(TypeOfString.FILE_PATH.getRegex())) {
            throw new InvalidInputException("Input could not be parsed to File due to: Invalid file path");
        }
        return new File(this.currentValue);
    }

    public BigDecimal asBigDecimal() throws NumberFormatException {
        return BigDecimal.valueOf(Double.parseDouble(this.currentValue));
    }

    public Float asFloat() throws NumberFormatException {
        return Float.parseFloat(this.currentValue);
    }

    public Double asDouble() throws NumberFormatException {
        return Double.parseDouble(this.currentValue);
    }

}
