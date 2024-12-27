package org.collection.fm.formulagraph;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.TiernanSimpleCycles;

import java.util.*;

public class InterruptableTiernanSimpleCycles<V, E> extends TiernanSimpleCycles<V, E> {

    public InterruptableTiernanSimpleCycles(Graph<V, E> graph) {
        super(graph);
    }


    public long countSimpleCycles() {
        long count = 0;
        if (this.getGraph() == null) {
            throw new IllegalArgumentException("Null graph.");
        } else {
            Map<V, Integer> indices = new HashMap<>();
            List<V> path = new ArrayList<>();
            Set<V> pathSet = new HashSet<>();
            Map<V, Set<V>> blocked = new HashMap<>();
            int index = 0;

            for (V v : this.getGraph().vertexSet()) {
                if (Thread.currentThread().isInterrupted()) return -1;
                blocked.put(v, new HashSet<>());
                indices.put(v, index++);
            }

            Iterator<V> vertexIterator = this.getGraph().vertexSet().iterator();
            if (vertexIterator.hasNext()) {
                V endOfPath = vertexIterator.next();
                path.add(endOfPath);
                pathSet.add(endOfPath);

                while (true) {
                    if (Thread.currentThread().isInterrupted()) return -1;
                    boolean extensionFound = false;

                    for (E e : this.getGraph().outgoingEdgesOf(endOfPath)) {
                        if (Thread.currentThread().isInterrupted()) return -1;
                        V n = this.getGraph().getEdgeTarget(e);
                        int cmp =  indices.get(n).compareTo(indices.get(path.getFirst()));
                        if (cmp > 0 && !pathSet.contains(n) && ! blocked.get(endOfPath).contains(n)) {
                            path.add(n);
                            pathSet.add(n);
                            endOfPath = n;
                            extensionFound = true;
                            break;
                        }
                    }

                    if (!extensionFound) {
                        V startOfPath = path.getFirst();
                        if (this.getGraph().containsEdge(endOfPath, startOfPath)) {
                            count++;
                        }

                        if (path.size() > 1) {
                            blocked.get(endOfPath).clear();
                            int endIndex = path.size() - 1;
                            path.remove(endIndex);
                            pathSet.remove(endOfPath);
                            --endIndex;
                            V temp = endOfPath;
                            endOfPath = path.get(endIndex);
                            blocked.get(endOfPath).add(temp);
                        } else {
                            if (!vertexIterator.hasNext()) {
                                return count;
                            }

                            path.clear();
                            pathSet.clear();
                            endOfPath = vertexIterator.next();
                            path.add(endOfPath);
                            pathSet.add(endOfPath);

                            for (var vt : blocked.values()) {
                                if (Thread.currentThread().isInterrupted()) return -1;
                                vt.clear();
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

}
