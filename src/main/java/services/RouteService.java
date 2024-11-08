package services;

import entities.Airport;
import entities.Route;
import org.apache.logging.log4j.Level;
import utils.LoggerService;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
     * Retrieves the optimal route path between two specified airports, based on the provided route metric.
     * <p>
     * Uses the Floyd-Warshall algorithm to calculate the optimal path, allowing for selection
     * between shortest and cheapest paths through the `routeFunction` parameter.
     * If no path exists between the airports, an empty list is returned.
     *
     * @param start         The starting airport for the route.
     * @param end           The destination airport for the route.
     * @param airports      A list of available airports to reference for indices in the graph.
     * @param routeFunction A function defining the metric for route calculation (e.g., Route::getKm for distance, Route::getPrice for cost).
     * @return A list of Route objects representing the optimal path between the start and end airports, or an empty list if no route exists.
     */
    public static List<Route> getRoutesBetweenAirports(Airport start, Airport end, List<Airport> airports, ToDoubleFunction<Route> routeFunction) {
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
            return List.of();
        }

        double[][] graph = makeGraph(airports, routes, routeFunction);
        List<List<List<Integer>>> paths = new ArrayList<>();
        applyFloydWarshall(graph, paths);

        if (graph[startIndex][endIndex] == INF) {
            LoggerService.consoleLog(Level.INFO, "No available path between the selected airports.");
            return List.of();
        }

        List<Integer> pathAirportIndex = paths.get(startIndex).get(endIndex);

        List<Route> pathRoutes = new ArrayList<>();
        for (int i = 0; i < pathAirportIndex.size() - 1; i++) {
            int fromIndex = pathAirportIndex.get(i);
            int toIndex = pathAirportIndex.get(i + 1);

            Airport airportFrom = airports.get(fromIndex);
            Airport airportTo = airports.get(toIndex);

            routes.stream()
                    .filter(r -> r.getIdFrom() == airportFrom.getId() && r.getIdTo() == airportTo.getId())
                    .min(Comparator.comparingDouble(routeFunction)).ifPresent(pathRoutes::add);
        }

        return pathRoutes;
    }


    /**
     * Prints the details of a route path between two airports, including each segment's distance and price,
     * as well as the total distance and total cost for the entire route.
     * <p>
     * If no route exists between the selected airports, a message is logged indicating the absence of a path.
     * Each route segment is prefixed by a number for easy reference, displaying both departure and arrival
     * airports for each segment.
     *
     * @param routePath The list of Route objects representing the path between the start and end airports.
     * @param start     The starting airport for the route.
     * @param end       The destination airport for the route.
     * @param airports  The list of all available airports, used to retrieve airport names based on route information.
     */
    public static void printRouteDetails(List<Route> routePath, Airport start, Airport end, List<Airport> airports) {
        if (routePath.isEmpty()) {
            LoggerService.consoleLog(Level.INFO, "No available path between the selected airports.");
            return;
        }

        System.out.printf("Route from %s to %s:\n", start.getName(), end.getName());

        AtomicInteger index = new AtomicInteger(1);
        String details = routePath.stream()
                .map(route -> String.format(
                        "%d - %s -> %s (%d km, $%.2f)",
                        index.getAndIncrement(),
                        findAirport(airports, route.getIdFrom()).getName(),
                        findAirport(airports, route.getIdTo()).getName(),
                        route.getKm(), route.getPrice()
                ))
                .collect(Collectors.joining("\n"));

        System.out.println(details);

        int totalKm = routePath.stream().mapToInt(Route::getKm).sum();
        double totalPrice = routePath.stream().mapToDouble(Route::getPrice).sum();
        System.out.printf("Total: %d km, $%.2f\n\n", totalKm, totalPrice);
    }

    private static Airport findAirport(List<Airport> airports, int airportId) {
        return airports.stream()
                .filter(a -> a.getId() == airportId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Airport not found"));
    }

}
