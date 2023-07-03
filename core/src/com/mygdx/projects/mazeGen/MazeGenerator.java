package com.mygdx.projects.mazeGen;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.Graph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.projects.utils.garphs.GraphImpl;

import java.util.Random;

public final class MazeGenerator<N> {

    private final static Random rand = new Random();
    private final Array<Connection<N>> freeNode = new Array<>(4);
    private final Queue<N> queue = new Queue<>(10);
    private final ObjectSet<N> visited = new ObjectSet<>(10);

    private void clear() {
        freeNode.clear();
        queue.clear();
        visited.clear();
    }

    public GraphImpl<N> generate(Graph<N> graph, N fromNode) {
        clear();

        GraphImpl<N> defaultGraph = new GraphImpl<>();

        queue.addLast(fromNode);
        while (queue.size > 0) {
            N currentNode = queue.last();
            visited.add(currentNode);
            Array<Connection<N>> connections = graph.getConnections(currentNode);
            freeNode.clear();

            for (Connection<N> c : connections) {
                if (!visited.contains(c.getToNode())) {
                    freeNode.add(c);
                }
            }

            int indexFreeNeighbour;

            if (freeNode.size == 1) {
                indexFreeNeighbour = 0;
            } else if (freeNode.size > 1) {
                indexFreeNeighbour = rand.nextInt(freeNode.size);
            } else {
                queue.removeLast();
                continue;
            }

            Connection<N> connection = freeNode.get(indexFreeNeighbour);
            connections = graph.getConnections(connection.getToNode());

            defaultGraph.addConnection(connection);
            for (Connection<N> c : connections) {
                if (c.getToNode().equals(currentNode)) {
                    defaultGraph.addConnection(c);
                    break;
                }
            }
            queue.addLast(connection.getToNode());
        }

        return defaultGraph;
    }

    public GraphImpl<N> generate(Graph<N> graph, N fromNode, int numNodes) {
        visited.ensureCapacity(numNodes - visited.size);
        return generate(graph, fromNode);
    }
}
