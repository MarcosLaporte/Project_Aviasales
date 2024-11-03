package utils.input.visitors;

import utils.input.single_input.StringInput;

public final class GetValueVisitor implements ValueInputVisitor {

    public String visitString(StringInput stringInput){
        return stringInput.getValue();
    }
}
