package services;

import entities.Airport;
import entities.Route;
import org.apache.logging.log4j.Level;
import utils.LoggerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;


public abstract class RouteService {

    public static final int INF = Integer.MAX_VALUE / 2;

    /**
     * Creates the graph with every row and column as a destination, having te direct distance between them as
     * the value inside the matrix, if there is none is INF, the index are align with the list sent by param
     * for example airports.get(0) is the row 0 in the matrix
     *
     * @param airports List of airports to be represented in the graph.
     * @param routes   List of routes connecting airports with distances.
     * @return A matrix representing the distances between each airport pair.
     */
    private static double[][] makeKmGraph(List<Airport> airports, List<Route> routes) {
        return makeGraph(airports, routes, Route::getKm);
    }

    private static double[][] makePriceGraph(List<Airport> airports, List<Route> routes) {
        return makeGraph(airports, routes, Route::getPrice);
    }

    private static double[][] makeGraph(List<Airport> airports, List<Route> routes, ToDoubleFunction<Route> function) {
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
     * Applies the Floyd-Warshall algorithm to compute the shortest paths and track each path.
     * Updates the graph with the shortest distances and the paths list with corresponding paths.
     *
     * @param graph  The distance matrix between airports.
     * @param parent The parent matrix used to track paths.
     * @param paths  A 3D list storing the paths for each airport pair.
     */
    private static void applyFloydWarshall(double[][] graph, int[][] parent, List<List<List<Integer>>> paths) {
        int V = graph.length;
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                for (int k = 0; k < V; k++) {
                    if (graph[j][i] + graph[i][k] < graph[j][k]) {
                        parent[j][k] = parent[i][k];
                        graph[j][k] = graph[j][i] + graph[i][k];

                        List<Integer> newPath = new ArrayList<>(paths.get(j).get(i));

                        newPath.removeLast();
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
    private static int[][] initializeParentArray(double[][] graph, List<List<List<Integer>>> paths) {
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

    private static List<Route> getRoutes() {
        try (MyBatis<Route> routesDAO = new MyBatis<>(Route.class)) {
            return routesDAO.get(Map.of());
        } catch (Exception e) {
            LoggerService.log(Level.ERROR, e.getMessage() != null ? e.getMessage() : e.getClass().toString());
        }

        return List.of();
    }

    /**
     * This should print the nearest route between airports
     *
     * @param startIndex start of the trip
     * @param endIndex   destination of the trip
     * @param airports   airport list
     */
    public static void printRouteBetweenAirports(int startIndex, int endIndex, List<Airport> airports) {
        List<Route> routes = getRoutes();
        if (routes.isEmpty()) {
            LoggerService.log(Level.ERROR, "No routes found in database.");
        }

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
            System.out.println(kmPaths.get(startIndex).get(endIndex)
                    .stream().map(index -> airports.get(index).getName())
                    .collect(Collectors.joining(" -> ")));
            System.out.println(" (Distance: " + kmGraph[startIndex][endIndex] + " km)");
        } else {
            System.out.println("No available path between the selected airports by distance.");
        }

        // Cheapest route
        if (startIndex != endIndex && priceGraph[startIndex][endIndex] < INF) {
            System.out.println("Cheapest path from " + airports.get(startIndex).getName() +
                    " to " + airports.get(endIndex).getName() + ":");
            System.out.println(pricePaths.get(startIndex).get(endIndex)
                    .stream().map(index -> airports.get(index).getName())
                    .collect(Collectors.joining(" -> ")));
            System.out.println(" (Price: $" + priceGraph[startIndex][endIndex] + ")");
        } else {
            System.out.println("No available path between the selected airports by price.");
        }
    }

}
