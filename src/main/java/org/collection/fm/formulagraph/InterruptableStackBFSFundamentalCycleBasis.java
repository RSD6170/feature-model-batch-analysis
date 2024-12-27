package org.collection.fm.formulagraph;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.StackBFSFundamentalCycleBasis;


import java.util.*;
import java.util.stream.Collectors;

public class InterruptableStackBFSFundamentalCycleBasis<V, E> extends StackBFSFundamentalCycleBasis<V,E> {

    public InterruptableStackBFSFundamentalCycleBasis(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    protected Map<V, E> computeSpanningForest() {
        Map<V, E> pred = new HashMap<>();
        ArrayDeque<V> stack = new ArrayDeque<>();

        for(V s : this.graph.vertexSet()) {
            if (Thread.currentThread().isInterrupted()) return Map.of();
            if (!pred.containsKey(s)) {
                pred.put(s, null);
                stack.push(s);

                while(!stack.isEmpty()) {
                    V v = stack.pop();

                    for(E e : this.graph.edgesOf(v)) {
                        if (Thread.currentThread().isInterrupted()) return Map.of();
                        V u = Graphs.getOppositeVertex(this.graph, e, v);
                        if (!pred.containsKey(u)) {
                            pred.put(u, e);
                            stack.push(u);
                        }
                    }
                }
            }
        }

        return pred;
    }

    public long getCycleBasisCount() {
        long count = 0;
        Map<V, E> spanningForest = this.computeSpanningForest();
        if (spanningForest.isEmpty()) return -1; //handle interrupt in computeSpanningForest
        Set<E> treeEdges = spanningForest.values().stream().filter(Objects::nonNull).collect(Collectors.toSet());

        for(E e : this.graph.edgeSet()) {
            if (Thread.currentThread().isInterrupted()) return -1;
            if (!treeEdges.contains(e)) {
                count++;
            }
        }

        return count;
    }

}
