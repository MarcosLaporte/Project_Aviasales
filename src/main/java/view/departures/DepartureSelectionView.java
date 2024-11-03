package view.departures;

import entities.Airport;
import view.general.SelectionMenu;

import java.util.List;

public final class DepartureSelectionView extends SelectionMenu<Airport> {

    public DepartureSelectionView(List<Airport> departurePoints){
        super(departurePoints);
        setConfiguration();
    }

    protected void setConfiguration(){
        setMenuMessage("Welcome to Aviasales!" +
                "\nPlease, select a departure point.");

        setElementConsumer((Airport e, Integer index) -> {
            System.out.printf("%d - %s\n", index, e.getName());
        });
    }

}
