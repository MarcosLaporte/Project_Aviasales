package view;

import utils.InputService;

import java.util.List;
import java.util.function.BiConsumer;

public class SelectionMenuView<T> {

    /** Minimum number for options */
    protected int min;

    /** Maximum number for options */
    protected int max;

    /** List of elements to list as options */
    protected List<T> options;

    /** Message that gets printed when menu view is displayed */
    protected String menuMessage = "Please, select one of the following options:";

    /** Consumer that allows to set behaviors to display each element. Default consumer prints the index and the element's string representation */
    protected BiConsumer<T, Integer> elementConsumer = (e, index) -> System.out.printf("%d. %s\n", min + index, e);

    /**
     * Constructor that allows for setting the list of options.
     * menuMessage and elementConsumer should be initialized with their respective methods.
     * */
    public SelectionMenuView(List<T> options) {
        setOptions(options);
    }

    /**
     * Default constructor for SelectionMenuView
     * */
    public SelectionMenuView() {
    }

    /**
     * Displays the view.
     * Prints out the set message (if not set, prints default message),
     * Then, prints each element according to the set consumer.
     * Then, prints exit option.
     * */
    public void display() {
        System.out.println(menuMessage);
        for (int i = 0; i < options.size(); i++) {
            elementConsumer.accept(options.get(i), i + 1);
        }
        System.out.printf("%d. Exit.\n", max);
    }

    /**
     * Allows to set the menu message
     *
     * @param menuMessage the message
     * @return Instance of SelectionMenuView for method chaining
     */
    public final SelectionMenuView<T> setMenuMessage(String menuMessage) {
        this.menuMessage = menuMessage;
        return this;
    }

    /**
     * Allows to set the elementConsumer for the menu elements
     *
     * @param elementConsumer the consumer that allows to set each element's display logic
     * @return Instance of SelectionMenuView for method chaining
     */
    public final SelectionMenuView<T> setElementConsumer(BiConsumer<T, Integer> elementConsumer) {
        this.elementConsumer = elementConsumer;
        return this;
    }

    /**
     * Allows to set the list of options that will be displayed.
     *
     * @param options The set of options.
     * @return Instance of SelectionMenuView for method chaining
     */
    public final SelectionMenuView<T> setOptions(List<T> options) {
        min = 1;
        max = options.size() + 1;
        this.options = options;
        return this;
    }

    /**
     * Allows to read the option selected from the user in the CLI.
     */
    protected final int retrieveInput() {
        // Return validated Input
        return InputService.readNumber(
                "Please, introduce the desired option: ",
                "This option does not exist. Try again: ",
                min, max, Integer.class);
    }
}
