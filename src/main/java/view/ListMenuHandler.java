package view;

import org.apache.logging.log4j.Level;
import utils.LoggerService;

import java.util.function.BiConsumer;

public class ListMenuHandler<T> {

    /** View related to the menu. Displays the options of the menu on the CLI. */
    private SelectionMenuView<T> selectionMenuView;

    /**
     * Consumer that allows for setting the behaviors for each menu option.
     * Allows for usage of a lambda with the menu type (for the menu object) and an Integer (the displayed index) as parameters.
     * */
    private BiConsumer<T, Integer> processOption;

    /**
     * Loop flag.
     *  Allows repeating the menu once an option's behavior has finished without errors.
     * */
    private boolean loop = false;

    /**
     * Constructor for ListMenuHandler object.
     * Allows for object creation with an already existing view.
     * Sets the passed view as the current selectionMenuView.
     * Requires option consumer to be set with setOptionConsumer method.
     *
     * @param view The current view for the menu
     */
    public ListMenuHandler(SelectionMenuView<T> view) {
        this.selectionMenuView = view;
    }

    /** Default constructor for ListMenuHandler object.
     * Requires option consumer, view and loop flag (if necessary) to be set using the class methods.
     * */
    public ListMenuHandler() {
    }

    /**
     * Displays the menu view, retrieves user selection,
     * and starts a routine to handle the selected option's behavior.
     */
    public final void show() {
        LoggerService.fileLog(Level.TRACE, "Entering show");

        // Guard to prevent execution if the menu view has not been set.
        LoggerService.fileLog(Level.TRACE, "Checking if selectionMenuView is null");
        if (selectionMenuView == null) {
            throw new RuntimeException("Menu view has not been set.");
        }
        LoggerService.fileLog(Level.TRACE, "Displaying menu view");
        this.selectionMenuView.display();

        // Input retrieval
        LoggerService.fileLog(Level.TRACE, "Retrieving input from user.");
        int selectedOption = selectionMenuView.retrieveInput(); // Displayed menu option
        int actualOptionIndex = selectedOption - 1;             // Actual index in selectionMenuView.options array
        
        boolean selectedInvalidOption = selectedOption < selectionMenuView.min 
                                        || selectedOption > selectionMenuView.max;
        boolean selectedExitOption = selectedOption == selectionMenuView.max;

        try {
            LoggerService.fileLog(Level.DEBUG, String.format("Menu option selected: %s\n", selectedOption));

            if (selectedInvalidOption) {
                LoggerService.fileLog(Level.DEBUG, "Invalid option selected");
                System.out.println("Invalid option selected. Re-displaying view.menu.");
                show();
            }

            if (selectedExitOption) {
                LoggerService.fileLog(Level.DEBUG, "Exit option selected");
                System.out.println("Shutting down program.");
                System.exit(0);
            }

            // Guard to prevent wrong execution if a consumer has not been set.
            LoggerService.fileLog(Level.DEBUG, "Checking if processOption is null");
            if (processOption == null){
                throw new RuntimeException("No process consumer has been set.");
            }

            // Retrieves option to process
            LoggerService.fileLog(Level.DEBUG, "Retrieving option from array of options");
            T optionToProcess = selectionMenuView.options.get(actualOptionIndex);

            // Executes established consumer for options.
            LoggerService.fileLog(Level.DEBUG, String.format("Processing option: %s", optionToProcess));
            processOption.accept(optionToProcess, selectedOption);
            LoggerService.fileLog(Level.ERROR, "Some random error");
        } catch (NumberFormatException e) {
            String msg = String.format("Invalid utils.input format for view.menu option selection: %s", e.getMessage());
            LoggerService.fileLog(Level.ERROR, msg);
            LoggerService.log(Level.ERROR, msg);
        } catch (RuntimeException e){
            String msg = String.format("Error while processing view menu option selection: %s", e.getMessage());
            LoggerService.log(Level.ERROR, msg);
            LoggerService.fileLog(Level.ERROR, msg);
        } catch (Exception e) {
            String msg = String.format("An unexpected error occurred: %s", e.getMessage());
            LoggerService.log(Level.ERROR, msg);
            LoggerService.fileLog(Level.ERROR, msg);
        }

        if (loop) {
            show();
        }

        LoggerService.fileLog(Level.TRACE, "Exiting show");
    }

    /**
     * Sets a consumer that defines the behavior for each menu option,
     * which works with the generic type T and the option's displayed index.
     * Conforms with method chaining
     *
     * @param consumer BiConsumer that uses the option object and its displayed index on the menu
     * @return Instance of ListMenuHandler&lt;T&gt; for method chaining
     */

    public final ListMenuHandler<T> setOptionConsumer(BiConsumer<T, Integer> consumer) {
        this.processOption = consumer;
        return this;
    }

    /**
     * Sets the menu loop flag.
     * Allows for menu looping when an option's consumer has terminated its execution without errors.
     * loop flag is disabled by default.
     *
     * @param loop boolean value for loop flag (true enables it, false disables it)
     * @return Instance of ListMenuHandler&lt;T&gt; for method chaining
     */
    public final ListMenuHandler<T> setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }
}

