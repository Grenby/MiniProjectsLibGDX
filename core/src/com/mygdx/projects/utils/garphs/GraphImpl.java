package com.mygdx.projects.utils.garphs;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.Graph;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Set;

public class GraphImpl<N> implements Graph<N> {

    protected HashMap<N, Array<Connection<N>>> connections = new HashMap<>();

    public GraphImpl() {
    }

    public Set<N> getNodes() {
        return connections.keySet();
    }

    @Override
    public Array<Connection<N>> getConnections(N fromNode) {
        return connections.get(fromNode);
    }

    public void addConnection(Connection<N> connection) {
        addNode(connection.getFromNode());
        addNode(connection.getToNode());
        Array<Connection<N>> connectionArray = connections.get(connection.getFromNode());
        connectionArray.add(connection);
    }

    public void addConnection(N from, N to) {
        addConnection(new DefaultConnection<>(from, to));
    }

    public void addDoubleConnection(N from, N to) {
        addConnection(new DefaultConnection<>(from, to));
        addConnection(new DefaultConnection<>(to, from));
    }

    public void addNode(N node) {
        if (!connections.containsKey(node)) {
            connections.put(node, new Array<>());
        }
    }
}
