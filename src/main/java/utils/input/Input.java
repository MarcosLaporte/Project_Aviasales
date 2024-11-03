package utils.input;

import utils.input.visitors.OperationInputVisitor;
public interface Input {
    public void accept(OperationInputVisitor visitor);
}
