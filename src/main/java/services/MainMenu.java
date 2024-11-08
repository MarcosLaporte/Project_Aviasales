package services;

import entities.Airport;
import entities.Passenger;
import entities.Route;
import entities.Trip;
import org.apache.logging.log4j.Level;
import utils.InputService;
import utils.LoggerService;
import view.ListMenuHandler;
import view.SelectionMenuView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

        mainMenu.show();
    }

    private static void handleNewTrip() {
        try (
                MyBatis<Airport> airportDao = new MyBatis<>(Airport.class);
                MyBatis<Trip> tripDao = new MyBatis<>(Trip.class)
        ) {
            Passenger currentPassenger = getCurrentPassenger();
            LoggerService.consoleLog(Level.INFO, "Welcome, " + currentPassenger.getFullName());

            List<Airport> airports = airportDao.get();
            List<Airport> chosenAirports = selectAirports(airports);

            RouteService routeService = new RouteService(airports, chosenAirports.get(0), chosenAirports.get(1));
            List<Route> shortestRoutes = routeService.getRoutesBetweenAirports(Route::getKm);
            List<Route> cheapestRoutes = routeService.getRoutesBetweenAirports(Route::getPrice);

            routeService.printRouteDetails(shortestRoutes);
            routeService.printRouteDetails(cheapestRoutes);

            List<Route> chosenRoutes = chooseFinalTrip(shortestRoutes, cheapestRoutes);
            saveTripToDatabase(tripDao, currentPassenger, chosenRoutes);

            LoggerService.consoleLog(Level.INFO, "Trip saved to database!");
            routeService.saveSession(shortestRoutes, cheapestRoutes);
        } catch (IOException e) {
            LoggerService.consoleLog(Level.ERROR, e.getMessage());
        }
    }

    private static Passenger getCurrentPassenger() {
        try (MyBatis<Passenger> passengerDao = new MyBatis<>(Passenger.class)) {
            List<Passenger> passengers = passengerDao.get();

            String[] passengerIds = passengers.stream()
                    .map(Passenger::getId)
                    .map(String::valueOf)
                    .toArray(String[]::new);

            String passengerIdStr = InputService.readString(
                    "Enter passenger ID: ",
                    "This ID does not belong to a passenger. Try again: ",
                    passengerIds
            );

            return passengers.stream()
                    .filter(p -> p.getId() == Integer.parseInt(passengerIdStr))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Passenger not found with ID: " + passengerIdStr));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Airport> selectAirports(List<Airport> airports) {
        SelectionMenuView<Airport> airportsView = new SelectionMenuView<>(airports)
                .setElementConsumer((Airport a, Integer index) -> System.out.printf("%d. %s\n", index, a))
                .setMenuMessage("Select a departure airport: ");
        List<Airport> chosenAirports = new ArrayList<>();

        ListMenuHandler<Airport> airportMenuHandler = new ListMenuHandler<>(airportsView)
                .setOptionConsumer((a, _) -> {
                    chosenAirports.add(a);
                    airports.remove(a);
                });

        airportMenuHandler.show(); // Departure

        airportsView
                .setOptions(airports)
                .setMenuMessage("Select destination: ");
        airportMenuHandler.show(); // Arrival

        airports.add(chosenAirports.get(0)); // Add back selected airports
        airports.add(chosenAirports.get(1));

        LoggerService.println(chosenAirports.get(0) + " -> " + chosenAirports.get(1));
        return chosenAirports;
    }

    private static List<Route> chooseFinalTrip(List<Route> shortestRoutes, List<Route> cheapestRoutes) {
        int chosenTrip = InputService.readCharInValues(
                "Select final trip. Shortest (1) or Cheapest (2): ",
                "Invalid value. Try again (1-2): ",
                new char[]{'1', '2'}
        );
        return chosenTrip == '1' ? shortestRoutes : cheapestRoutes;
    }

    private static void saveTripToDatabase(MyBatis<Trip> tripDao, Passenger currentPassenger, List<Route> chosenRoutes) {
        for (int i = 0; i < chosenRoutes.size(); i++) {
            Route route = chosenRoutes.get(i);
            LocalDate date = LocalDate.now().plusDays(i);
            Trip trip = new Trip(currentPassenger.getId(), route.getId(), date);
            tripDao.create(trip);
        }
    }

}