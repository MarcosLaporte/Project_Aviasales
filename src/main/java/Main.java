import entities.Airport;
import entities.Entity;
import entities.Route;
import org.apache.logging.log4j.Level;
import services.IDao;
import services.MyBatis;
import services.RouteService;
import utils.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

//        int[][] graph = {
//                {0, 2800, Global.INF, 3500, Global.INF},
//                {Global.INF, 0, 8500, Global.INF, 11700},
//                {2500, Global.INF, 0, 4000, Global.INF},
//                {Global.INF, Global.INF, 4000, 0, 11500},
//                {7200, Global.INF, Global.INF, 1500, 0}
//        };
//
//        List<Airport> airports = getAirports();
//        List<String> airportsNames = new ArrayList<>();
//        for (Airport a : airports) {
//            airportsNames.add(a.getName());
//        }
//        HashMap<String, HashMap<String, Integer>> distances = PathFindingService.shortestPath(airportsNames, graph, Global.INF);
//
//        distances.forEach((key, value) -> {
//            System.out.println(key);
//            value.forEach((l, d) -> System.out.println(l + " " + d));
//        });

        RouteService routeService = new RouteService();
        routeService.start();
    }
}
