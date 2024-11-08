package services;

import entities.Airline;
import entities.Airport;
import entities.Entity;
import entities.Route;
import org.apache.logging.log4j.Level;
import utils.EntityReflection;
import utils.LoggerService;
import view.ListMenuHandler;
import view.SelectionMenuView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainMenu {
    private enum Menu {
        NEW_AIRLINE, NEW_AIRPORT, NEW_ROUTE, NEW_TRIP;

        @Override
        public String toString() {
            return super.toString().replace('_', ' ');
        }
    }

    public static void start() {
        SelectionMenuView<Menu> view = new SelectionMenuView<>(List.of(Menu.values()))
                .setElementConsumer((Menu e, Integer index) -> System.out.printf("%d. %s\n", index, e))
                .setMenuMessage("Select an action to perform: ");

        ListMenuHandler<Menu> mainMenu = new ListMenuHandler<>(view)
                .setLoop(true)
                .setOptionConsumer((e, _) -> {
                    switch (e) {
                        case NEW_AIRLINE -> createEntity(Airline.class);
                        case NEW_AIRPORT -> createEntity(Airport.class);
                        case NEW_ROUTE -> createEntity(Route.class);
                        case NEW_TRIP -> handleNewTrip();
                    }
                });

        mainMenu.processMenuOption();
    }

    private static <T extends Entity> void createEntity(Class<T> clazz) {
        EntityReflection<T> rs = new EntityReflection<>(clazz);

        try (MyBatis<T> dao = new MyBatis<>(clazz)) {
            if (dao.create(rs.readNewInstance(false)) > 0)
                LoggerService.println(clazz.getSimpleName() + " created!");
            else
                LoggerService.println("No " + clazz.getSimpleName() + " was created.");
        } catch (Exception e) {
            LoggerService.log(Level.ERROR, e.getMessage() != null ? e.getMessage() : e.getClass().toString());
        }
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