import entities.Airport;
import view.departures.SelectDepartureMenuHandler;
import view.general.ListMenuHandler;
import view.general.MenuElement;
import view.general.SelectionMenuView;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static Airport someAirport = null;

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

        exampleC();
    }

    public static void exampleA(List<Airport> airports) {
        ListMenuHandler<Airport> menu = new SelectDepartureMenuHandler(airports);
    }

    public static void exampleB(List<Airport> airports){
        SelectionMenuView<Airport> view = new SelectionMenuView<>(airports)
                .setElementConsumer((Airport e, Integer index) -> System.out.printf("%d - %s\n", index, e.getName()))
                .setMenuMessage("Welcome to Aviasales!\nPlease, select a departure point.\n");

        ListMenuHandler<Airport> menu2 = new ListMenuHandler<>(airports)
                .setView(view)
                .setOptionConsumer((e, index) -> System.out.printf("Selected option %d with object %s\n", index, e));
    }

    public static void exampleC(){
        List<MenuElement> options = Arrays.asList(
                new MenuElement("Set airport A", Main::funA),
                new MenuElement("Set airport B", Main::funB),
                new MenuElement("Set airport C", Main::funC)
        );

        SelectionMenuView<MenuElement> view = new SelectionMenuView<>(options)
                .setElementConsumer(
                        (MenuElement e, Integer index) -> System.out.printf("%d - %s\n", index, e.getName()))
                .setMenuMessage("Welcome to Aviasales!\nPlease, select an option.\n");

        ListMenuHandler<MenuElement> menu = new ListMenuHandler<>(options)
                .setLoop(true)
                .setView(view)
                .setOptionConsumer((e, index) -> {
                    System.out.printf("Selected option %d. Executing action:\n", index);
                    e.getRoutine().execute();
                });

        menu.processMenuOption();
    }

    public static void funA(){
        Main.someAirport = new Airport("A");
        System.out.println(Main.someAirport.getName());


        List<MenuElement> options = Arrays.asList(
                new MenuElement("Option A", Main::funA),
                new MenuElement("Option B", Main::funB),
                new MenuElement("Option C", Main::funC)
        );

        SelectionMenuView<MenuElement> view = new SelectionMenuView<>(options)
                .setElementConsumer(
                        (MenuElement e, Integer index) -> System.out.printf("%d - %s\n", index, e.getName()))
                .setMenuMessage("Welcome to secondary menu!\nPlease, select an option.\n");

        ListMenuHandler<MenuElement> menu = new ListMenuHandler<>(options)
                .setView(view)
                .setOptionConsumer((e, index) -> {
                    System.out.printf("Selected option %d. Executing action:\n", index);
                    e.getRoutine().execute();
                });

        menu.processMenuOption();
    }

    public static void funB(){
        Main.someAirport = new Airport("B");
        System.out.println(Main.someAirport.getName());
    }

    public static void funC(){
        Main.someAirport = new Airport("C");
        System.out.println(Main.someAirport.getName());

    }
}
