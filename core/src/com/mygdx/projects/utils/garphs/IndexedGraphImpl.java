package com.mygdx.projects.utils.garphs;

import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;

import java.util.HashMap;

public class IndexedGraphImpl<N> extends GraphImpl<N> implements IndexedGraph<N> {

    protected HashMap<N, Integer> indexes = new HashMap<>();
    protected int nextIndex = 0;

    @Override
    public int getIndex(N node) {
        return indexes.get(node);
    }

    @Override
    public int getNodeCount() {
        return indexes.size();
    }

    @Override
    public void addNode(N node) {
        super.addNode(node);
        if (!indexes.containsKey(node)) {
            indexes.put(node, nextIndex);
            nextIndex++;
        }
    }

}
