package view.departures;

import entities.Airport;
import view.general.ListMenuHandler;

import java.util.List;

public class SelectDepartureMenuHandler extends ListMenuHandler<Airport> {

    public SelectDepartureMenuHandler(List<Airport> departurePoints){
        super(departurePoints);
        setConfiguration();
    }

    protected void setConfiguration() {
        setView(new DepartureSelectionView(objects));
        setOptionConsumer((e, index) -> System.out.printf("Selected option %d with object %s\n", index, e));
    }
}

/* Usage:
* List<Airport> airports = Arrays.asList(
                new Airport(1, "A"),
                new Airport(2, "B"),
                new Airport(3, "C"));

        ListMenuHandler<Airport> menu = new SelectDeparture(airports);

        menu.processMenuOption();
* */
