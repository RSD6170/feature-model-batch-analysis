package org.collection.fm.formulagraph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.StackBFSFundamentalCycleBasis;
import org.jgrapht.alg.cycle.TiernanSimpleCycles;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.prop4j.Node;
import org.prop4j.Or;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;

public class ConnectivityGraph {

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
    public static int getNumberOfIndependentCycles(FeatureModelFormula formula) {
        Graph<String, DefaultEdge> graph = initializeGraph(formula, false);
        StackBFSFundamentalCycleBasis<String, DefaultEdge> computer = new StackBFSFundamentalCycleBasis<>(graph);
        return computer.getCycleBasis().getCycles().size();
    }

    public static int getNumberOfCycles(FeatureModelFormula formula) {
        Graph<String, DefaultEdge> graph = initializeGraph(formula, true);
        TiernanSimpleCycles<String, DefaultEdge> computer = new TiernanSimpleCycles<>(graph);
        return computer.findSimpleCycles().size();
    }

}
