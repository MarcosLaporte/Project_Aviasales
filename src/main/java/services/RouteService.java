package services;

import entities.Airport;
import entities.Route;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LoggerService;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.util.*;
import java.util.function.ToDoubleFunction;


public class RouteService {

    public static final int INF = Integer.MAX_VALUE / 2;
    private static final Logger log = LogManager.getLogger(RouteService.class);

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
        printAllKmRoutes();
        printAllPriceRoutes();

    }


    private void printAllKmRoutes() {
        List<Airport> airports = getAirports();
        List<Route> routes = getRoutes();

        double[][] graph = makeKmGraph(airports, routes);
        int[][] parent;
        List<List<List<Integer>>> paths = new ArrayList<>();

        parent = initializeParentArray(graph, paths);

        applyFloydWarshall(graph, parent, paths);

        printAllRoutes(graph, paths, "Shortest Paths and Distances:", "Distance");
    }

    private void printAllPriceRoutes() {
        List<Airport> airports = getAirports();
        List<Route> routes = getRoutes();

        double[][] graph = makePriceGraph(airports, routes);
        int[][] parent;
        List<List<List<Integer>>> paths = new ArrayList<>();

        parent = initializeParentArray(graph, paths);

        applyFloydWarshall(graph, parent, paths);

        printAllRoutes(graph, paths, "Cheapest Paths and Prices:", "Price");
    }

    private void printAllRoutes(double[][] graph, List<List<List<Integer>>> paths, String tittle, String sortingValue) {
        List<Airport> airports = getAirports();
        int V = graph.length;
        System.out.println(tittle);
        for (int i = 0; i < V; i++) {
            LoggerService.println("From: " + airports.get(i).getName());
            for (int j = 0; j < V; j++) {
                if (i != j && graph[i][j] < INF) {
                    LoggerService.println("To: " + airports.get(j).getName());
                    paths.get(i).get(j).forEach(node -> System.out.print(airports.get(node).getName() + " -> "));
                    System.out.println(" (" + sortingValue + ": " + graph[i][j] + ")");
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
     */
    private double[][] makeKmGraph(List<Airport> airports, List<Route> routes) {
        return makeGraph(airports, routes, Route::getKm);
    }

    private double[][] makePriceGraph(List<Airport> airports, List<Route> routes) {
        return makeGraph(airports, routes, Route::getPrice);
    }

    private double[][] makeGraph(List<Airport> airports, List<Route> routes, ToDoubleFunction<Route> function) {
        double[][] graph = new double[airports.size()][airports.size()];

        for (int i = 0; i < airports.size(); i++) {
            Airport airportFrom = airports.get(i);

            for (int j = 0; j < airports.size(); j++) {

                Airport airportTo = airports.get(j);

                if (i == j) {
                    graph[i][j] = 0;
                } else {
                    for (Route r : routes) {
                        if (r.getIdFrom() == airportFrom.getId() && r.getIdTo() == airportTo.getId()) {
                            graph[i][j] = function.applyAsDouble(r);
                            break;
                        } else {
                            graph[i][j] = INF;
                        }
                    }
                }
            }
        }
        return graph;
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
    private void applyFloydWarshall(double[][] graph, int[][] parent, List<List<List<Integer>>> paths) {
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
     * @param graph The distance matrix for airport routes.
     * @param paths A 3D list to hold each path from source to destination.
     * @return An initialized parent matrix to be used in path updates.
     */
    private int[][] initializeParentArray(double[][] graph, List<List<List<Integer>>> paths) {
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

        // distance graph
        double[][] kmGraph = makeKmGraph(airports, routes);
        int[][] kmParent;
        List<List<List<Integer>>> kmPaths = new ArrayList<>();

        kmParent = initializeParentArray(kmGraph, kmPaths);
        applyFloydWarshall(kmGraph, kmParent, kmPaths);

        // price graph
        double[][] priceGraph = makePriceGraph(airports, routes);
        int[][] priceParent;
        List<List<List<Integer>>> pricePaths = new ArrayList<>();

        priceParent = initializeParentArray(priceGraph, pricePaths);
        applyFloydWarshall(priceGraph, priceParent, pricePaths);

        // shortest route
        if (startIndex != endIndex && kmGraph[startIndex][endIndex] < INF) {
            System.out.println("Shortest path from " + airports.get(startIndex).getName() +
                    " to " + airports.get(endIndex).getName() + ":");
            kmPaths.get(startIndex).get(endIndex).forEach(index ->
                    System.out.print(airports.get(index).getName() + " -> "));
            System.out.println(" (Distance: " + kmGraph[startIndex][endIndex] + " km)");
        } else {
            System.out.println("No available path between the selected airports by distance.");
        }

        // Cheapest route
        if (startIndex != endIndex && priceGraph[startIndex][endIndex] < INF) {
            System.out.println("Cheapest path from " + airports.get(startIndex).getName() +
                    " to " + airports.get(endIndex).getName() + ":");
            pricePaths.get(startIndex).get(endIndex).forEach(index ->
                    System.out.print(airports.get(index).getName() + " -> "));
            System.out.println(" (Price: $" + priceGraph[startIndex][endIndex] + ")");
        } else {
            System.out.println("No available path between the selected airports by price.");
        }
    }

}
