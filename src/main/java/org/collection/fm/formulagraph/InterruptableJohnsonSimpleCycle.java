package org.collection.fm.formulagraph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.IntrusiveEdgeException;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import java.util.*;

public class InterruptableJohnsonSimpleCycle<V, E>  {

    protected static final Logger LOGGER = LogManager.getLogger();

    private final Graph<V, E> graph;
    private long count = 0;
    private V[] iToV = null;
    private Map<V, Integer> vToI = null;
    private Set<V> blocked = null;
    private Map<V, Set<V>> bSets = null;
    private ArrayDeque<V> stack = null;
    private List<Set<V>> foundSCCs = null;
    private int index = 0;
    private Map<V, Integer> vIndex = null;
    private Map<V, Integer> vLowlink = null;
    private ArrayDeque<V> path = null;
    private Set<V> pathSet = null;

    public InterruptableJohnsonSimpleCycle(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
        if (GraphTests.hasMultipleEdges(graph)) {
            throw new IllegalArgumentException("Graph should not have multiple (parallel) edges");
        }
    }

    public long countSimpleCycles() throws InterruptedException {
        if (this.graph == null) {
            throw new IllegalArgumentException("Null graph.");
        } else {
            this.initState();
            int startIndex = 0;

            for(int size = this.graph.vertexSet().size(); startIndex < size; ++startIndex) {
                Pair<Graph<V, E>, Integer> minSCCGResult = this.findMinSCSG(startIndex);
                if (minSCCGResult == null) {
                    break;
                }

                startIndex = minSCCGResult.getSecond();
                Graph<V, E> scg = minSCCGResult.getFirst();
                V startV = this.toV(startIndex);

                for(E e : scg.outgoingEdgesOf(startV)) {
                    if (Thread.interrupted()) throw new InterruptedException();
                    V v = this.graph.getEdgeTarget(e);
                    this.blocked.remove(v);
                    this.getBSet(v).clear();
                }

                this.findCyclesInSCG(startIndex, startIndex, scg);
            }

            this.clearState();
        }
        return count;
    }

    private Pair<Graph<V, E>, Integer> findMinSCSG(int startIndex) throws InterruptedException {
        this.initMinSCGState();
        List<Set<V>> foundSCCs = this.findSCCS(startIndex);
        int minIndexFound = Integer.MAX_VALUE;
        Set<V> minSCC = null;

        for(Set<V> scc : foundSCCs) {
            for(V v : scc) {
                if (Thread.interrupted()) throw new InterruptedException();

                int t = this.toI(v);
                if (t < minIndexFound) {
                    minIndexFound = t;
                    minSCC = scc;
                }
            }
        }

        if (minSCC == null) {
            return null;
        } else {
            Graph<V, E> resultGraph = GraphTypeBuilder.directed().edgeSupplier(this.graph.getEdgeSupplier()).vertexSupplier(this.graph.getVertexSupplier()).allowingMultipleEdges(false).allowingSelfLoops(true).buildGraph();

            for(V v : minSCC) {
                resultGraph.addVertex(v);
            }

            for(V v : minSCC) {
                for(V w : minSCC) {
                    if (Thread.interrupted()) throw new InterruptedException();

                    E edge = this.graph.getEdge(v, w);
                    if (edge != null) {
                        try {
                            resultGraph.addEdge(v, w);
                        } catch (IntrusiveEdgeException e){
                            LOGGER.warn("Intrusive edge detected", e);
                        }
                    }
                }
            }

            Pair<Graph<V, E>, Integer> result = Pair.of(resultGraph, minIndexFound);
            this.clearMinSCCState();
            return result;
        }
    }

    private List<Set<V>> findSCCS(int startIndex) throws InterruptedException {
        for(V v : this.graph.vertexSet()) {
            if (Thread.interrupted()) throw new InterruptedException();

            int vI = this.toI(v);
            if (vI >= startIndex && !this.vIndex.containsKey(v)) {
                this.getSCCs(startIndex, vI);
            }
        }

        List<Set<V>> result = this.foundSCCs;
        this.foundSCCs = null;
        return result;
    }

    private void getSCCs(int startIndex, int vertexIndex) throws InterruptedException {
        V vertex = this.toV(vertexIndex);
        this.vIndex.put(vertex, this.index);
        this.vLowlink.put(vertex, this.index);
        ++this.index;
        this.path.push(vertex);
        this.pathSet.add(vertex);

        for(E e : this.graph.outgoingEdgesOf(vertex)) {
            if (Thread.interrupted()) throw new InterruptedException();

            V successor = this.graph.getEdgeTarget(e);
            int successorIndex = this.toI(successor);
            if (successorIndex >= startIndex) {
                if (!this.vIndex.containsKey(successor)) {
                    this.getSCCs(startIndex, successorIndex);
                    this.vLowlink.put(vertex, Math.min(this.vLowlink.get(vertex), this.vLowlink.get(successor)));
                } else if (this.pathSet.contains(successor)) {
                    this.vLowlink.put(vertex, Math.min(this.vLowlink.get(vertex), this.vIndex.get(successor)));
                }
            }
        }

        if (this.vLowlink.get(vertex).equals(this.vIndex.get(vertex))) {
            Set<V> result = new HashSet<>();

            V temp;
            do {
                if (Thread.interrupted()) throw new InterruptedException();

                temp = this.path.pop();
                this.pathSet.remove(temp);
                result.add(temp);
            } while(!vertex.equals(temp));

            if (result.size() == 1) {
                V v = result.iterator().next();
                if (this.graph.containsEdge(vertex, v)) {
                    this.foundSCCs.add(result);
                }
            } else {
                this.foundSCCs.add(result);
            }
        }

    }

    private boolean findCyclesInSCG(int startIndex, int vertexIndex, Graph<V, E> scg) throws InterruptedException {
        boolean foundCycle = false;
        V vertex = this.toV(vertexIndex);
        this.stack.push(vertex);
        this.blocked.add(vertex);

        for(E e : scg.outgoingEdgesOf(vertex)) {
            if (Thread.interrupted()) throw new InterruptedException();

            V successor = scg.getEdgeTarget(e);
            int successorIndex = this.toI(successor);
            if (successorIndex == startIndex) {
                count++;
                foundCycle = true;
            } else if (!this.blocked.contains(successor)) {
                boolean gotCycle = this.findCyclesInSCG(startIndex, successorIndex, scg);
                foundCycle = foundCycle || gotCycle;
            }
        }

        if (foundCycle) {
            this.unblock(vertex);
        } else {
            for(E ew : scg.outgoingEdgesOf(vertex)) {
                if (Thread.interrupted()) throw new InterruptedException();

                V w = scg.getEdgeTarget(ew);
                Set<V> bSet = this.getBSet(w);
                bSet.add(vertex);
            }
        }

        this.stack.pop();
        return foundCycle;
    }

    private void unblock(V vertex) throws InterruptedException {
        this.blocked.remove(vertex);
        Set<V> bSet = this.getBSet(vertex);

        while(!bSet.isEmpty()) {
            if (Thread.interrupted()) throw new InterruptedException();

            V w = bSet.iterator().next();
            bSet.remove(w);
            if (this.blocked.contains(w)) {
                this.unblock(w);
            }
        }

    }

    private void initState() {
        this.count = 0;
        this.iToV = (V[]) this.graph.vertexSet().toArray();
        this.vToI = new HashMap<>();
        this.blocked = new HashSet<>();
        this.bSets = new HashMap<>();
        this.stack = new ArrayDeque<>();

        for(int i = 0; i < this.iToV.length; ++i) {
            this.vToI.put(this.iToV[i], i);
        }

    }

    private void clearState() {
        this.iToV = null;
        this.vToI = null;
        this.blocked = null;
        this.bSets = null;
        this.stack = null;
    }

    private void initMinSCGState() {
        this.index = 0;
        this.foundSCCs = new ArrayList<>();
        this.vIndex = new HashMap<>();
        this.vLowlink = new HashMap<>();
        this.path = new ArrayDeque<>();
        this.pathSet = new HashSet<>();
    }

    private void clearMinSCCState() {
        this.index = 0;
        this.foundSCCs = null;
        this.vIndex = null;
        this.vLowlink = null;
        this.path = null;
        this.pathSet = null;
    }

    private Integer toI(V vertex) {
        return this.vToI.get(vertex);
    }

    private V toV(Integer i) {
        return this.iToV[i];
    }

    private Set<V> getBSet(V v) {
        return this.bSets.computeIfAbsent(v, k -> new HashSet<>());
    }

}
