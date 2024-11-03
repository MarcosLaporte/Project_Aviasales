package utils.input;

import utils.input.visitors.ValueInputVisitor;

public abstract class SingleInput<T> implements Input{
    protected String name;
    protected String displayName;
    protected T value;

    public SingleInput(){
        name = "";
        displayName = "";
        value = null;
    }

    public SingleInput(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
        value = null;
    }

    public SingleInput(String name, String displayName, T value) {
        this.name = name;
        this.displayName = displayName;
        this.value = value;
    }

    public abstract String accept(ValueInputVisitor visitor);

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
