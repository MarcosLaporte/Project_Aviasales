package utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Level;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Predicate;

public abstract class InputService {
    private static final Scanner SCANNER = new Scanner(System.in);

    static {
        SCANNER.useLocale(Locale.US);
    }

    /**
     * Reads an integer from the user within a specified range. If input is invalid, displays an error message and prompts again.
     *
     * @param msg      the prompt message displayed to the user.
     * @param errorMsg the error message displayed upon invalid input.
     * @param min      the minimum allowable integer value.
     * @param max      the maximum allowable integer value.
     * @return the validated integer input within the specified range.
     */
    public static int readInt(String msg, String errorMsg, int min, int max) {
        LoggerService.print(msg);
        int inputNumber = min;
        boolean isValid = false;

        while (!isValid) {
            try {
                inputNumber = SCANNER.nextInt();
                Validate.inclusiveBetween(min, max, inputNumber);

                isValid = true;
            } catch (Exception e) {
                LoggerService.print(errorMsg);
            } finally {
                SCANNER.nextLine(); //Cleans buffer
            }
        }

        return inputNumber;
    }

    /**
     * Reads a floating-point number from the user within a specified range. If input is invalid, displays an error message and prompts again.
     *
     * @param msg      the prompt message displayed to the user.
     * @param errorMsg the error message displayed upon invalid input.
     * @param min      the minimum allowable float value.
     * @param max      the maximum allowable float value.
     * @return the validated float input within the specified range.
     */
    public static float readFloat(String msg, String errorMsg, float min, float max) {
        LoggerService.print(msg);
        float inputNumber = min;
        boolean isValid = false;

        while (!isValid) {
            try {
                inputNumber = SCANNER.nextFloat();
                Validate.inclusiveBetween(min, max, inputNumber);

                isValid = true;
            } catch (Exception e) {
                LoggerService.print(errorMsg);
                SCANNER.nextLine();
            } finally {
                SCANNER.nextLine(); //Cleans buffer
            }
        }

        return inputNumber;
    }

    /**
     * Reads a character from the user that must be one of the specified allowed values. If input is invalid, displays an error message and prompts again.
     *
     * @param msg             the prompt message displayed to the user.
     * @param errorMsg        the error message displayed upon invalid input.
     * @param availableValues the array of allowed characters.
     * @return the validated character input.
     */
    public static char readCharInValues(String msg, String errorMsg, char[] availableValues) {
        LoggerService.print(msg);
        char inputChar = Character.toUpperCase(SCANNER.next().charAt(0));
        while (!ArrayUtils.contains(availableValues, inputChar)) {
            LoggerService.print(errorMsg);
            inputChar = Character.toUpperCase(SCANNER.next().charAt(0));
        }

        SCANNER.nextLine(); //Cleans buffer
        return inputChar;
    }

    /**
     * Reads a yes or no confirmation from the user, allowing only 'Y' or 'N' as valid inputs.
     *
     * @param msg the prompt message displayed to the user.
     * @return true if the user inputs 'Y', otherwise false.
     */
    public static boolean readConfirmation(String msg) {
        return readCharInValues(
                msg,
                "Invalid value. Try again (Y/N): ",
                new char[]{'Y', 'N'}
        ) == 'Y';
    }

    /**
     * Reads a string from the user that must fall within specified length constraints. If input is invalid, displays an error message and prompts again.
     *
     * @param msg       the prompt message displayed to the user.
     * @param minLength the minimum allowable length of the string.
     * @param maxLength the maximum allowable length of the string.
     * @return the validated string input within the specified length.
     */
    public static String readString(String msg, int minLength, int maxLength) {
        LoggerService.print(msg);
        String inputStr;

        do {
            try {
                inputStr = SCANNER.nextLine();
                Validate.notBlank(inputStr);
                Validate.inclusiveBetween(minLength, maxLength, inputStr.length(),
                        String.format("String must be between %d and %d characters long.", minLength, maxLength));
            } catch (Exception e) {
                LoggerService.consoleLog(Level.WARN, e.getMessage());
                inputStr = StringUtils.EMPTY;
                LoggerService.print("Try again: ");
            }
        } while (StringUtils.isEmpty(inputStr));

        return inputStr;
    }

    /**
     * Reads a string from the user that must match one of the specified allowed values. If input is invalid, displays an error message and prompts again.
     *
     * @param msg             the prompt message displayed to the user.
     * @param errorMsg        the error message displayed upon invalid input.
     * @param availableValues the array of allowed strings.
     * @return the validated string input.
     */
    public static String readStringInValues(String msg, String errorMsg, String[] availableValues) {
        LoggerService.print(msg);
        String inputStr = SCANNER.next();
        while (!ArrayUtils.contains(availableValues, inputStr)) {
            LoggerService.print(errorMsg);
            inputStr = SCANNER.next();
        }

        SCANNER.nextLine(); //Cleans buffer

        return inputStr;
    }

    /**
     * Reads a string from the user that must satisfy a specified condition. If input is invalid, displays an error message and prompts again.
     *
     * @param msg       the prompt message displayed to the user.
     * @param errorMsg  the error message displayed upon invalid input.
     * @param condition a predicate defining the validation condition for the input.
     * @return the validated string input.
     */
    public static String readStringFollowsCondition(String msg, String errorMsg, Predicate<String> condition) {
        LoggerService.print(msg);
        String inputStr;

        do {
            try {
                inputStr = SCANNER.nextLine();
                if (!condition.test(inputStr)) {
                    throw new IllegalArgumentException(errorMsg);
                }
            } catch (Exception e) {
                LoggerService.println(e.getMessage());
                inputStr = StringUtils.EMPTY;
                LoggerService.print("Try again: ");
            }
        } while (StringUtils.isEmpty(inputStr));

        return inputStr;
    }

    /**
     * Prompts the user to select an index from a list of items with the option to cancel. Returns the index of the selected item.
     *
     * @param selectMessage the message displayed to the user for selection.
     * @param items         the list of items to choose from.
     * @param cancelMessage the message for the cancellation option, or null if not applicable.
     * @return the index of the selected item in the list, or -1 if the cancel option is selected.
     */
    public static int selectIndexFromList(String selectMessage, List<String> items, String cancelMessage) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append('\n').append(i + 1).append(". ");
            sb.append(items.get(i));
        }

        int min = 1;
        if (cancelMessage != null) {
            min = 0;
            sb.append("\n0. ").append(cancelMessage);
        }

        LoggerService.println(sb);
        return InputService.readInt(
                selectMessage,
                "Invalid value. Try again: ",
                min, items.size()
        ) - 1;
    }

    /**
     * Reads a date from the user by prompting for year, month, and day, or allows using the current date. Validates and returns the input date.
     *
     * @return the validated LocalDate input by the user or the current date if chosen.
     */
    public static LocalDate readValidDate() {
        int year = 0, month = 0, day = 0;
        boolean isValid = false;
        LocalDate date = null;

        char useCurrent = InputService.readCharInValues("Use current date? Y/N: ", "ERROR. Input Y or N: ", new char[]{'Y', 'N'});
        if (useCurrent == 'Y')
            return LocalDate.now();

        while (!isValid) {
            try {
                year = readInt(
                        "Enter year: ",
                        "Invalid year. Try again: ",
                        LocalDate.MIN.getYear(), LocalDate.MAX.getYear()
                );

                month = readInt(
                        "Enter month (1-12): ",
                        "Invalid month. Try again: ",
                        1, 12
                );

                day = readInt(
                        "Enter day: ",
                        "Invalid day. Try again: ",
                        1, 31
                );

                date = LocalDate.of(year, month, day);
                isValid = true;

            } catch (DateTimeException | IllegalArgumentException e) {
                LoggerService.println(String.format("'%d-%d-%d' Invalid date. Please try again.", year, month, day));
            }
        }

        return date;
    }
}
