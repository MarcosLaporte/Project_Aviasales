import Services.PathFindingService;
import utils.AivenDatabaseConnection;
import utils.Global;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        int[][] graph = {
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

        AivenDatabaseConnection.testConnection();
    }
}
