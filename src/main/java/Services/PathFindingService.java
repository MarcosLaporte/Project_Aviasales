package Services;

import java.util.HashMap;

//floydWarshall algorithm
public class PathFindingService {
    /**
     * dist = matrix containing each destination as indexes for i and j and having
     * the direct distance between them as value and an infinite value when
     * there is no direct path
     * example:
     * • = there is no direct route between destinations
     *   A | B | C | D | E
     * A 0 | 4 | • | 5 | •
     * B • | 0 | 1 | • | 6
     * C 2 | • | 0 | 3 | •
     * D • | • | 1 | 0 | 2
     * E 1 | • | • | 4 | 0
     * dist[A][A] -> 0 cause between A and A there is no distance, that goes for every time i = j
     * dist[A][B] -> 4 but dist[B][A] -> • cause you can go directly from A to B but not from B to A
     */
    private static void floydWarshall(int[][] dist, int INF) {
        System.out.println(INF);
        int V = dist.length;
        int i, j, k;
        for (k = 0; k < V; k++) {
            for (i = 0; i < V; i++) {
                for (j = 0; j < V; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j])
                        dist[i][j] = dist[i][k] + dist[k][j];
                }
            }
        }
    }

    public static HashMap<Character, HashMap<Character, Integer>> shortestPath(int[][] dist, int INF) {
        char[] letters = "ABCDEFGHI".toCharArray();
        floydWarshall(dist, INF);
        HashMap<Character, HashMap<Character, Integer>> distances = new HashMap<>();
        for (int i = 0; i < dist.length; ++i) {
            HashMap<Character, Integer> distance = new HashMap<>();
            for (int j = 0; j < dist.length; ++j) {
                if (dist[i][j] != INF)
                    distance.put(letters[j], dist[i][j]);
            }
            distances.put(letters[i], distance);
        }
        return distances;
    }
}
