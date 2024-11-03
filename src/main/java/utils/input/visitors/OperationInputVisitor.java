package utils.input.visitors;

import utils.input.single_input.StringInput;


public interface OperationInputVisitor {
    void visitString (StringInput stringInput);
}
