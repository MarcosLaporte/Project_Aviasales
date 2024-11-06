import entities.Airport;
import view.departures.SelectDepartureMenuHandler;
import view.general.ListMenuHandler;
import view.general.SelectionMenuView;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        /*int[][] graph = {
                {0, 4, Global.INF, 5, Global.INF},
                {Global.INF, 0, 1, Global.INF, 6},
                {2, Global.INF, 0, 3, Global.INF},
                {Global.INF, Global.INF, 1, 0, 2},
                {1, Global.INF, Global.INF, 4, 2}
        };

        HashMap<Character, HashMap<Character, Integer>> distances = PathFindingService.shortestPath(graph, Global.INF);

        distances.forEach((key, value) -> {
            System.out.println(key);
            value.forEach((l, d) -> System.out.println(l + " " + d));
        });

        AivenDatabaseConnection.testConnection();*/

        List<Airport> airports = Arrays.asList(
                new Airport(1, "A"),
                new Airport(2, "B"),
                new Airport(3, "C"));

        ListMenuHandler<Airport> menu = new SelectDepartureMenuHandler(airports);

        // O tambien se puede

        SelectionMenuView<Airport> view = new SelectionMenuView<>(airports)
            .setElementConsumer((Airport e, Integer index) -> System.out.printf("%d - %s\n", index, e.getName()))
            .setMenuMessage("Welcome to Aviasales!\nPlease, select a departure point.\n");

        ListMenuHandler<Airport> menu2 = new ListMenuHandler<>(airports)
                .setView(view)
                .setOptionConsumer((e, index) -> System.out.printf("Selected option %d with object %s\n", index, e));

        menu.processMenuOption();
    }
}
