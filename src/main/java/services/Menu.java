package services;

public enum Menu {
    GET, CREATE, UPDATE, DELETE, NEW_TRIP;

    @Override
    public String toString() {
        return super.toString().replace('_', ' ');
    }
}
