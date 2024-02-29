package Model.ai;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;

    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public void addNode(Point point) {
        nodes.add(new Node(point));
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addEdge(Node source, Node destination, double weight) {
        edges.add(new Edge(source, destination, weight));
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
