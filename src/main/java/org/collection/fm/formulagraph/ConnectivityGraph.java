package org.collection.fm.formulagraph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.prop4j.Node;
import org.prop4j.Or;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;

import java.util.Objects;

public class ConnectivityGraph {

    private ConnectivityGraph(){
        //hide
    }

    private static final Logger LOGGER = LogManager.getLogger();

    private static Graph<String, DefaultEdge> initializeGraph(FeatureModelFormula formula, boolean isDirected) {
        LOGGER.debug("Begin graph initialization in FMBA Connectivity Graph");
        Graph<String, DefaultEdge> graph;
        if (isDirected) graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        else graph = new SimpleGraph<>(DefaultEdge.class);
        Node cnf = formula.getCNFNode();
        for (Node clause : cnf.getChildren()) {
            if (Thread.currentThread().isInterrupted()) break;
            handleClause(graph, clause, isDirected);
        }
        LOGGER.info("Finished graph initialization in FMBA Connectivity Graph with {} nodes and {} edges", () -> graph.vertexSet().size(), () -> graph.edgeSet().size());
        return graph;
    }

    private static void handleClause(Graph<String, DefaultEdge> graph, Node set, boolean isDirected) {
        if (set.getChildren().length < 2) {
            return;
        }
        if (!(set instanceof Or)) {
            LOGGER.error("NOT CNF");
            return;
        }
        for (int i = 0; i < set.getChildren().length - 1; i++) {
            for (int j = i + 1; j < set.getChildren().length; j++) {
                if (Thread.currentThread().isInterrupted()) return;
                var v1 = (String) set.getChildren()[i].getLiterals().getFirst().var;
                var v2 = (String) set.getChildren()[j].getLiterals().getFirst().var;
                if (Objects.equals(v1, v2)) continue; //no self-loops allowed
                Graphs.addEdgeWithVertices(graph, v1, v2);
                if (isDirected) Graphs.addEdgeWithVertices(graph, v2, v1);
            }
        }
    }

    public static int getNumberOfEdges(FeatureModelFormula formula) {
        Graph<String, DefaultEdge> graph = initializeGraph(formula, false);
        return graph.edgeSet().size();
    }

    // computes the number of independent cycles
    // i.e. the vertices in each cycle are not a subset of another one
    public static long getNumberOfIndependentCycles(FeatureModelFormula formula) {
        Graph<String, DefaultEdge> graph = initializeGraph(formula, false);
        InterruptableStackBFSFundamentalCycleBasis<String, DefaultEdge> computer = new InterruptableStackBFSFundamentalCycleBasis<>(graph);
        return computer.getCycleBasisCount();
    }

    public static long getNumberOfCycles(FeatureModelFormula formula) {
        Graph<String, DefaultEdge> graph = initializeGraph(formula, true);
        InterruptableJohnsonSimpleCycle<String, DefaultEdge> computer = new InterruptableJohnsonSimpleCycle<>(graph);
        try {
            return (computer.countSimpleCycles() - graph.edgeSet().size()) / 2; // taken from https://www.tandfonline.com/doi/abs/10.1080/18756891.2013.857893
        } catch (InterruptedException e) {
            LOGGER.debug("Interrupted numberOfCycles", e);
            Thread.currentThread().interrupt();
            return -1;
        }
    }

}
