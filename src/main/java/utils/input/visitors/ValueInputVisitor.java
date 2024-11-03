package utils.input.visitors;

import utils.input.single_input.StringInput;

public interface ValueInputVisitor {
    String visitString(StringInput input);
}
