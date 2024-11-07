package view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;

public class ListMenuHandler<T> {

    protected static final Logger logger = LogManager.getLogger(ListMenuHandler.class);
    private SelectionMenuView<T> selectionMenuView;
    private BiConsumer<T, Integer> processOption;
    private boolean loop = false;

    public ListMenuHandler(SelectionMenuView<T> view) {
        this.selectionMenuView = view;
    }

    public ListMenuHandler() {
    }

    public final void processMenuOption() {
        logger.trace("Entering processMenuOption");
        this.selectionMenuView.display();
        int selectedOption = selectionMenuView.retrieveInput();

        try {
            logger.debug("Menu option selected: {}\n", selectedOption);

            if (selectedOption < selectionMenuView.min || selectedOption > selectionMenuView.max) {
                System.out.println("Invalid option selected. Re-displaying view.menu.");
                processMenuOption();
            }

            if (selectedOption == selectionMenuView.max) {
                System.out.println("Shutting down program.");
                System.exit(0);
            }

            processOption.accept(selectionMenuView.options.get(selectedOption - 1), selectedOption);

        } catch (NumberFormatException e) {
            logger.error("Invalid utils.input format for view.menu option selection: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);
        }

        logger.trace("Exiting processMenuOption");
        if (loop) {
            processMenuOption();
        }
    }

    public final ListMenuHandler<T> setOptionConsumer(BiConsumer<T, Integer> consumer) {
        this.processOption = consumer;
        return this;
    }

    public final ListMenuHandler<T> setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }
}

