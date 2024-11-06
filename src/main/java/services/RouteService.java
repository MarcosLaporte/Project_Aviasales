package services;

import entities.Airport;
import entities.Route;
import org.apache.logging.log4j.Level;
import utils.LoggerService;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RouteService {

    public static final int INF = Integer.MAX_VALUE / 2;

    public void start() {
        //printAllRoutes();
        Scanner scanner = new Scanner(System.in);

        // Show available airports and tells the user to choose 2

        List<Airport> airports = getAirports();
        showAirports(airports);

        System.out.println("Enter the number of the starting airport:");
        int startIndex = scanner.nextInt();

        System.out.println("Enter the number of the destination airport:");
        int endIndex = scanner.nextInt();

        printRouteBetweenAirports(startIndex, endIndex, airports);
    }

    private void printAllRoutes() {
        List<Airport> airports = getAirports();
        List<Route> routes = getRoutes();

        int[][] graph = makeGraph(airports, routes);
        int V = graph.length;
        int[][] parent;
        /*Esto es lo mas importante, paths, es una lista de listas de listas, cuack, simplificado es una matriz como la
        * que venimos usando pero adentro de la matriz se guarda una lista que contiene los indices de los aeropuertos
        * por los que tenes que pasar para llegar a cada destino, osea si estas en la fila 2 columna 3, significa que
        * queres ir del aeropuerto 2 al 3, suponinedo que no hay un camino directo y el camino mas corto es pasar por el
        * 4 es decir, 2 -> 4 -> 3 entonces en paths.get(2).get(3) va a haber una lista que tiene los indices, [2,4,3]*/
        List<List<List<Integer>>> paths = new ArrayList<>();

        parent = initializeParentArray(graph, paths);

        applyFloydWarshall(graph, parent, paths);

        System.out.println("Shortest Paths and Distances:");
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (i != j && graph[i][j] < INF) {
                    System.out.print("Path from " + airports.get(i).getName() + " to " + airports.get(j).getName() + "\n-> ");

                    paths.get(i).get(j).forEach(node -> System.out.print(airports.get(node).getName() + " -> ") );

                    System.out.println(" (Distance: " + graph[i][j] + ")");
                }
            }
            System.out.println();
        }
    }

    /**
     * Creates the graph with every row and column as a destination, having te direct distance between them as
     * the value inside the matrix, if there is none is INF, the index are align with the list sent by param
     * for example airports.get(0) is the row 0 in the matrix
     *
     * @param airports List of airports to be represented in the graph.
     * @param routes   List of routes connecting airports with distances.
     * @return A matrix representing the distances between each airport pair.
     * */
    private int[][] makeGraph(List<Airport> airports, List<Route> routes) {
        int[][] kmGraph = new int[airports.size()][airports.size()];

        for (int i = 0; i < airports.size(); i++) {
            Airport airportFrom = airports.get(i);

            for (int j = 0; j < airports.size(); j++) {

                Airport airportTo = airports.get(j);

                if (i == j) {
                    kmGraph[i][j] = 0;
                } else {
                    for (Route r : routes) {
                        if (r.getIdFrom() == airportFrom.getId() && r.getIdTo() == airportTo.getId()) {
                            kmGraph[i][j] = r.getKm();
                            break;
                        } else {
                            kmGraph[i][j] = INF;
                        }
                    }
                }
            }
        }
        return kmGraph;
    }

    /**
     * FloydWarshall explanation
     * graph = matrix containing each destination as indexes for i and j and having
     * the direct distance between them as value and an infinite value when
     * there is no direct path
     * example:
     * • = there is no direct route between destinations
     * A | B | C | D | E
     * A 0 | 4 | • | 5 | •
     * B • | 0 | 1 | • | 6
     * C 2 | • | 0 | 3 | •
     * D • | • | 1 | 0 | 2
     * E 1 | • | • | 4 | 0
     * dist[A][A] -> 0 cause between A and A there is no distance, that goes for every time i = j
     * dist[A][B] -> 4 but dist[B][A] -> • cause you can go directly from A to B but not from B to A
     */
    /**
     * Applies the Floyd-Warshall algorithm to compute the shortest paths and track each path.
     * Updates the graph with the shortest distances and the paths list with corresponding paths.
     *
     * @param graph  The distance matrix between airports.
     * @param parent The parent matrix used to track paths.
     * @param paths  A 3D list storing the paths for each airport pair.
     */
    private void applyFloydWarshall(int[][] graph, int[][] parent, List<List<List<Integer>>> paths) {
        int V = graph.length;
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                for (int k = 0; k < V; k++) {
                    if (graph[j][i] + graph[i][k] < graph[j][k]) {
                        parent[j][k] = parent[i][k];
                        graph[j][k] = graph[j][i] + graph[i][k];

                        List<Integer> newPath = new ArrayList<>(paths.get(j).get(i));

                        newPath.remove(newPath.size() - 1);
                        newPath.addAll(paths.get(i).get(k));
                        paths.get(j).set(k, newPath);
                    }
                }
            }
        }
    }

    /**
     * Initializes the parent array and paths list for the Floyd-Warshall algorithm.
     * Each path initially consists of the start and end nodes of each direct route.
     *
     * @param graph  The distance matrix for airport routes.
     * @param paths  A 3D list to hold each path from source to destination.
     * @return An initialized parent matrix to be used in path updates.
     */
    private int[][] initializeParentArray(int[][] graph, List<List<List<Integer>>> paths){
        int V = graph.length;
        int[][] parent = new int[V][V];
        // Initialize parent array
        for (int i = 0; i < V; i++) {
            parent[i][i] = i;
            paths.add(new ArrayList<>());
            for (int j = 0; j < V; j++) {
                parent[i][j] = (i == j) ? i : (graph[i][j] != INF ? i : -1);

                List<Integer> path = new ArrayList<>();
                if (graph[i][j] != INF) {
                    path.add(i);
                    path.add(j);
                }
                paths.get(i).add(path);
            }
        }
        return parent;
    }

    private List<Route> getRoutes() {
        IDao<Route> routesDAO = null;
        try {
            routesDAO = new MyBatis<>(Route.class);
            List<Route> routes = routesDAO.get(Map.of());
            return routes;
        } catch (Exception e) {
            LoggerService.log(Level.ERROR, e.getMessage() != null ? e.getMessage() : e.getClass().toString());
        } finally {
            try {
                if (routesDAO != null && Closeable.class.isAssignableFrom(routesDAO.getClass()))
                    ((Closeable) routesDAO).close();
            } catch (IOException e) {
                LoggerService.log(Level.ERROR, e.getMessage());
            }
        }
        return List.of();
    }

    private List<Airport> getAirports() {
        IDao<Airport> airportsDAO = null;
        try {
            airportsDAO = new MyBatis<>(Airport.class);
            List<Airport> airports = airportsDAO.get(Map.of());
            return airports;
        } catch (Exception e) {
            LoggerService.log(Level.ERROR, e.getMessage() != null ? e.getMessage() : e.getClass().toString());
        } finally {
            try {
                if (airportsDAO != null && Closeable.class.isAssignableFrom(airportsDAO.getClass()))
                    ((Closeable) airportsDAO).close();
            } catch (IOException e) {
                LoggerService.log(Level.ERROR, e.getMessage());
            }
        }
        return List.of();
    }

    ///implementing asking for starting airport and ending airport


    /**
     * Shows airport list
     */
    private void showAirports(List<Airport> airports) {
        System.out.println("Available Airports:");
        for (int i = 0; i < airports.size(); i++) {
            System.out.println(i + ". " + airports.get(i).getName());
        }
    }

    /**
     * This should print the nearest route between airports
     *
     * @param startIndex start of the trip
     * @param endIndex destination of the trip
     * @param airports airport list
     */
    private void printRouteBetweenAirports(int startIndex, int endIndex, List<Airport> airports) {
        List<Route> routes = getRoutes();
        int[][] graph = makeGraph(airports, routes);
        int V = graph.length;
        int[][] parent;
        List<List<List<Integer>>> paths = new ArrayList<>();

        parent = initializeParentArray(graph, paths);
        applyFloydWarshall(graph, parent, paths);

        // here we get the shortest path from one to another
        if (startIndex != endIndex && graph[startIndex][endIndex] < INF) {
            System.out.println("Shortest path from " + airports.get(startIndex).getName() +
                    " to " + airports.get(endIndex).getName() + ":");
            paths.get(startIndex).get(endIndex).forEach(index ->
                    System.out.print(airports.get(index).getName() + " -> "));
            System.out.println(" (Distance: " + graph[startIndex][endIndex] + ")");
        } else {
            System.out.println("No available path between the selected airports.");
        }
    }

}
