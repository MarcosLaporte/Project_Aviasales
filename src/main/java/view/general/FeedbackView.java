package view.general;

import utils.input.single_input.StringInput;

import java.util.HashMap;

public abstract class FeedbackView extends View {

    protected HashMap<String, String> inputs;

    public FeedbackView() {
        super();
        inputs = new HashMap<>();
    }

    protected abstract void retrieveInputs();

    public HashMap<String, String> getInputs() {
        return inputs;
    }

    protected void processInputs(StringInput[] stringInputs){
        if (stringInputs == null){
            //Main.logger.error("Array of StringInputs is null. NullPointerException thrown.");
            throw new NullPointerException("Array of StringInput is null");
        }

        for (StringInput data : stringInputs){
            String inputName = data.getName();
            String currentValue = data.processInput().getValue();
            inputs.put(inputName, currentValue);
        }
    }
}
