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

        mainMenu.processMenuOption();
    }

    private static void handleNewTrip() {
        try (
                MyBatis<Airport> airportDao = new MyBatis<>(Airport.class);
                MyBatis<Trip> tripDao = new MyBatis<>(Trip.class)
        ) {
            Passenger currentPassenger = getCurrentPassenger();
            LoggerService.consoleLog(Level.INFO, "Welcome, " + currentPassenger.getFullName());

            List<Airport> airports = airportDao.get();
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

            List<Route> shortestRoutes = RouteService.getRoutesBetweenAirports(
                    chosenAirports.getFirst(),
                    chosenAirports.getLast(),
                    airports, Route::getKm
            );

            RouteService.printRouteDetails(
                    shortestRoutes,
                    chosenAirports.getFirst(),
                    chosenAirports.getLast(),
                    airports
            );

            List<Route> cheapestRoutes = RouteService.getRoutesBetweenAirports(
                    chosenAirports.getFirst(),
                    chosenAirports.getLast(),
                    airports, Route::getPrice
            );

            RouteService.printRouteDetails(
                    cheapestRoutes,
                    chosenAirports.getFirst(),
                    chosenAirports.getLast(),
                    airports
            );

            int chosenTrip = InputService.readCharInValues(
                    "Select final trip. Shortest (1) or Cheapest (2): ",
                    "Invalid value. Try again (1-2): ",
                    new char[]{'1', '2'}
            );
            final List<Route> chosenRoutes = chosenTrip == '1' ? shortestRoutes : cheapestRoutes;
            for (int i = 0; i < chosenRoutes.size(); i++) {
                Route route = chosenRoutes.get(i);
                LocalDate date = LocalDate.now().plusDays(i);
                Trip trip = new Trip(currentPassenger.getId(), route.getId(), date);
                tripDao.create(trip);
            }

            LoggerService.consoleLog(Level.INFO, "Trip saved to database!");
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
}