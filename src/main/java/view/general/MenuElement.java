package view.general;

public class MenuElement {
    String name = "";
    Routine routine = null;

    public MenuElement(String name, Routine routine){
        this.name = name;
        this.routine = routine;
    }

    public String getName(){
        return name;
    }

    public Routine getRoutine(){
        return routine;
    }
}
