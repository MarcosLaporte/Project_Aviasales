package view.menu;

import view.general.SelectionMenu;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class ListMenuHandler<T> extends MenuHandler {

    protected final List<T> objects;
    private final int min;
    private final int max;
    private SelectionMenu<T> selectionMenuView;
    private BiConsumer<T, Integer> processOption;

    public ListMenuHandler(List<T> objects){
        this.objects = objects;
        min = 1;
        max = objects.size() + 1;
    }

    @Override
    public final void processMenuOption() {
        logger.trace("Entering processMenuOption");
        selectionMenuView.display();
        int selectedOption = selectionMenuView.getSelectedOption();

        try {
            logger.debug("Menu option selected: {}", selectedOption);

            if(selectedOption < min || selectedOption > max){
                System.out.println("Invalid option selected. Re-displaying view.menu.");
                processMenuOption();
            }

            if (selectedOption == max){
                System.out.println("Shutting down program.");
                System.exit(0);
            }

            processOption.accept(objects.get(selectedOption - 1), selectedOption);

        } catch (NumberFormatException e) {
            logger.error("Invalid utils.input format for view.menu option selection: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);
        }

        logger.trace("Exiting processMenuOption");
        processMenuOption();
    }

    protected final void setView(SelectionMenu<T> view){
        this.selectionMenuView = view;
    }

    protected final void setOptionConsumer(BiConsumer<T, Integer> consumer){
        this.processOption = consumer;
    }

    protected abstract void setConfiguration();
}

