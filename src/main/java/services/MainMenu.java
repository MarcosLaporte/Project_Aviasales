package services;

import entities.Airport;
import utils.LoggerService;
import view.ListMenuHandler;
import view.SelectionMenuView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainMenu {
    public static void start() {
        SelectionMenuView<Menu> view = new SelectionMenuView<>(List.of(Menu.values()))
                .setElementConsumer((Menu e, Integer index) -> System.out.printf("%d. %s\n", index, e))
                .setMenuMessage("Select an action to perform: ");

        ListMenuHandler<Menu> mainMenu = new ListMenuHandler<>(view)
                .setLoop(true)
                .setOptionConsumer((e, _) -> {
                    if (e == Menu.NEW_TRIP)
                        handleNewTrip();
                    else
                        CrudMenu.handleCrudOperation(e);
                });

        mainMenu.processMenuOption();
    }

    private static void handleNewTrip() {
        try (MyBatis<Airport> airportDao = new MyBatis<>(Airport.class)) {
            List<Airport> airports = airportDao.get(Map.of());

            SelectionMenuView<Airport> airportsView = new SelectionMenuView<>(airports)
                    .setElementConsumer((Airport a, Integer index) -> System.out.printf("%d. %s\n", index, a))
                    .setMenuMessage("Select a departure airport: ");

            final List<Airport> chosenAirports = new ArrayList<>();

            ListMenuHandler<Airport> airportMenuHandler = new ListMenuHandler<>(airportsView)
                    .setOptionConsumer((a, _) -> {
                        chosenAirports.add(a);
                        airports.remove(a); // Remove selected airport to avoid duplicate selection
                    });

            airportMenuHandler.processMenuOption(); // Departure

            airportsView
                    .setOptions(airports) // Update items list
                    .setMenuMessage("Select destination: ");
            airportMenuHandler.processMenuOption(); // Arrival

            airports.add(chosenAirports.getFirst()); // Put back selected airports
            airports.add(chosenAirports.getLast()); // Put back selected airports
            LoggerService.println(chosenAirports.getFirst() + " -> " + chosenAirports.get(1));

            RouteService.printRouteBetweenAirports(
                    chosenAirports.getFirst(),
                    chosenAirports.getLast(),
                    airports
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}