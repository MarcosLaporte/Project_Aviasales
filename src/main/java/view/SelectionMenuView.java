package view;

import utils.InputService;

import java.util.List;
import java.util.function.BiConsumer;

public class SelectionMenuView<T> {
    protected int min;
    protected int max;
    protected List<T> options;
    protected String menuMessage = "Please, select one of the following options:";
    protected BiConsumer<T, Integer> elementConsumer = (e, index) -> System.out.printf("%d. %s\n", min + index, e);

    public SelectionMenuView(List<T> options) {
        setOptions(options);
    }

    public SelectionMenuView() {
    }

    public void display() {
        System.out.println(menuMessage);
        for (int i = 0; i < options.size(); i++) {
            elementConsumer.accept(options.get(i), i + 1);
        }
        System.out.printf("%d. Exit.\n", max);
    }

    public final SelectionMenuView<T> setMenuMessage(String menuMessage) {
        this.menuMessage = menuMessage;
        return this;
    }

    public final SelectionMenuView<T> setElementConsumer(BiConsumer<T, Integer> elementConsumer) {
        this.elementConsumer = elementConsumer;
        return this;
    }

    public final SelectionMenuView<T> setOptions(List<T> options) {
        min = 1;
        max = options.size() + 1;
        this.options = options;
        return this;
    }

    protected final int retrieveInput() {
        // Return validated Input
        return InputService.readNumber(
                "Please, introduce the desired option: ",
                "This option does not exist. Try again: ",
                min, max, Integer.class);
    }
}
