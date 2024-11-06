package entities;

import java.util.List;

public class GraphData {
    int[][] kmGraph;
    List<List<Route>> routesGraph;

    public GraphData(int[][] kmGraph, List<List<Route>> routesGraph) {
        this.kmGraph = kmGraph;
        this.routesGraph = routesGraph;
    }

    public int[][] getKmGraph() {
        return kmGraph;
    }

    public List<List<Route>> getRoutesGraph() {
        return routesGraph;
    }
}
