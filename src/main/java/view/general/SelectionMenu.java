package view.general;

import utils.input.single_input.StringInput;
import utils.input.single_input.StringInput.TypeOfString;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class SelectionMenu<T> extends FeedbackView {

    private String optionName = "menuOption";
    protected int min;
    protected int max;
    protected final List<T> options;
    protected String menuMessage = "Please, select one of the following options:";
    protected BiConsumer<T, Integer> elementConsumer = (e, index) -> System.out.printf("%d. %s\n", min + index, e);


    public SelectionMenu(List<T> options){
        this.options = options;
        min = 1;
        max = options.size() + 1;
    }

    public void display(){
        System.out.println(menuMessage);
        for(int i = 0; i < options.size(); i++){
            elementConsumer.accept(options.get(i), i + 1);
        }
        System.out.printf("%d. Exit.\n", max);
        retrieveInputs();
    }

    protected final SelectionMenu<T> setMenuMessage(String menuMessage){
        this.menuMessage = menuMessage;
        return this;
    }

    protected final SelectionMenu<T> setElementConsumer(BiConsumer<T, Integer> elementConsumer){
        this.elementConsumer = elementConsumer;
        return this;
    }

    protected final void retrieveInputs(){
        // Processes input
        StringInput input = new StringInput(optionName,   "Option", TypeOfString.NUMBER).processInput();
        int val = input.asInteger();
        // Guards minimum and maximum values
        if (val < min || val > max){
            System.out.println("Option is not valid, please try again.");
            retrieveInputs();
        }

        // If input is okay, set and return.
        inputs.put(input.getName(), input.getValue());
    }

    public final Integer getSelectedOption(){
        if (inputs.get(optionName) != null){
            return Integer.parseInt(inputs.get(optionName));
        }

        throw new RuntimeException("Inputs have not been processed");
    }



    protected abstract void setConfiguration();

}
