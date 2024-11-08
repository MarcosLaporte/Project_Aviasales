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

    public static final Double INF = Double.POSITIVE_INFINITY;

    /**
     * Creates the graph with every row and column as a destination, having te direct distance between them as
     * the value inside the matrix, if there is none is INF, the index are align with the list sent by param
     * for example airports.get(0) is the row 0 in the matrix
     *
     * @param airports List of airports to be represented in the graph.
     * @param routes   List of routes connecting airports with distances.
     * @return A matrix representing the distances between each airport pair.
     */
    private static double[][] makeGraph(List<Airport> airports, List<Route> routes, ToDoubleFunction<Route> function) {
        final int size = airports.size();
        double[][] graph = new double[size][size];

        Map<String, List<Route>> routeMap = routes.stream()
                .collect(Collectors.groupingBy(r -> r.getIdFrom() + "-" + r.getIdTo()));

        for (int i = 0; i < size; i++) {
            Airport airportFrom = airports.get(i);

            for (int j = 0; j < size; j++) {
                if (i == j) {
                    graph[i][j] = 0;
                    continue;
                }

                Airport airportTo = airports.get(j);

                List<Route> possibleRoutes = routeMap.get(airportFrom.getId() + "-" + airportTo.getId());
                if (possibleRoutes != null) {
                    graph[i][j] = possibleRoutes.stream()
                            .mapToDouble(function).min()
                            .orElse(INF);
                } else {
                    graph[i][j] = INF;
                }
            }
        }

        return graph;
    }

    /**
     * Applies the Floyd-Warshall algorithm to compute the shortest paths and track each path.
     * Updates the graph with the shortest distances and the paths list with corresponding paths.
     *
     * @param graph The distance matrix between airports.
     * @param paths A 3D list storing the paths for each airport pair.
     */
    private static void applyFloydWarshall(double[][] graph, List<List<List<Integer>>> paths) {
        initializeParentArray(graph, paths);
        final int V = graph.length;
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                for (int k = 0; k < V; k++) {
                    if (graph[j][i] + graph[i][k] < graph[j][k]) {
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
     */
    private static void initializeParentArray(double[][] graph, List<List<List<Integer>>> paths) {
        final int V = graph.length;
        for (int i = 0; i < V; i++) {
            paths.add(new ArrayList<>());
            for (int j = 0; j < V; j++) {
                List<Integer> path = new ArrayList<>();
                if (graph[i][j] != INF) {
                    path.add(i);
                    path.add(j);
                }
                paths.get(i).add(path);
            }
        }
    }

    private static List<Route> getRoutes() {
        try (MyBatis<Route> routesDAO = new MyBatis<>(Route.class)) {
            return routesDAO.get(Map.of());
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Prints the shortest and cheapest routes between two airports using pre-calculated distances and prices.
     * Calculates and displays the path, total distance, and price if routes exist between the airports.
     *
     * @param start    The departure airport to look for in the list.
     * @param end      The destination airport to look for in the list.
     * @param airports The list of available airports, each represented by an Airport object.
     */
    public static void printRouteBetweenAirports(Airport start, Airport end, List<Airport> airports) {
        List<Route> routes;
        final int startIndex;
        final int endIndex;
        try {
            if (start == end)
                throw new Exception("Same airport selected as departure and destination.");

            startIndex = airports.indexOf(start);
            endIndex = airports.indexOf(end);
            if (startIndex == -1 || endIndex == -1)
                throw new Exception("The airport list does not contain specified airports.");

            routes = getRoutes();
            if (routes.isEmpty())
                throw new Exception("No routes found in database.");
        } catch (Exception e) {
            LoggerService.log(Level.ERROR, e.getMessage());
            return;
        }

        // Distance graph
        double[][] kmGraph = makeGraph(airports, routes, Route::getKm);
        List<List<List<Integer>>> kmPaths = new ArrayList<>();
        applyFloydWarshall(kmGraph, kmPaths);

        // Price graph
        double[][] priceGraph = makeGraph(airports, routes, Route::getPrice);
        List<List<List<Integer>>> pricePaths = new ArrayList<>();
        applyFloydWarshall(priceGraph, pricePaths);

        if (kmGraph[startIndex][endIndex] == INF || priceGraph[startIndex][endIndex] == INF) {
            LoggerService.consoleLog(Level.INFO, "No available path between the selected airports.");
            return;
        }

        // Shortest route
        System.out.printf("Shortest route from %s to %s:\n", airports.get(startIndex).getName(), airports.get(endIndex).getName());

        List<Integer> shortestPathAirlineIndex = kmPaths.get(startIndex).get(endIndex);
        System.out.println(shortestPathAirlineIndex.stream()
                .map(index -> airports.get(index).getName() + " (" + kmGraph[startIndex][index] + " km)")
                .collect(Collectors.joining(" -> "))
                + '\n');


        // Cheapest route
        System.out.printf("Cheapest route from %s to %s:\n", airports.get(startIndex).getName(), airports.get(endIndex).getName());

        List<Integer> cheapestPathAirlineIndex = pricePaths.get(startIndex).get(endIndex);
        System.out.println(cheapestPathAirlineIndex.stream()
                .map(index -> airports.get(index).getName() + " ($" + priceGraph[startIndex][index] + ")")
                .collect(Collectors.joining(" -> "))
                + '\n');
    }

}
