package org.collection.fm.formulagraph;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.alg.cycle.StackBFSFundamentalCycleBasis;
import org.jgrapht.alg.cycle.TiernanSimpleCycles;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.prop4j.Node;
import org.prop4j.Or;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;

public class ConnectivityGraph {

    private static final Logger LOGGER = LogManager.getLogger();

    private Graph<String, DefaultEdge> graph;
    private CycleBasisAlgorithm.CycleBasis<String, DefaultEdge> cycleBasis;
    private Map<DefaultEdge, Integer> edgeIDMap;
    private FeatureModelFormula formula;


    private void initializeGraph(FeatureModelFormula formula) {
        if (! Objects.equals(formula, this.formula)) {
            this.graph = new SimpleGraph<>(DefaultEdge.class);
            Node cnf = formula.getCNFNode();
            for (Node clause : cnf.getChildren()) {
                if (Thread.currentThread().isInterrupted()) break;
                handleClause(clause);
            }
            edgeIDMap = HashMap.newHashMap(graph.edgeSet().size());
            int i = 0;
            for (DefaultEdge e : graph.edgeSet()) {
                edgeIDMap.put(e, i);
                i++;
            }
        }
    }

    private void handleClause(Node set) {
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
                Graphs.addEdgeWithVertices(graph, (String) set.getChildren()[i].getLiterals().getFirst().var, (String) set.getChildren()[j].getLiterals().getFirst().var);
            }
        }
    }

    public long getNumberOfEdges(FeatureModelFormula formula) {
        initializeGraph(formula);
        return graph.edgeSet().size();
    }

    // performs a dfs to compute the number of cycles
    // Algorithm from https://dl.acm.org/doi/epdf/10.1145/321541.321545 or http://dspace.mit.edu/bitstream/handle/1721.1/68106/FTL_R_1982_07.pdf, page 14 (16 in PDF)
    public long getNumberOfCycles(FeatureModelFormula formula) {
        initializeGraph(formula);
        if (cycleBasis == null) computeCycleBasis();

        List<BitSet> fundamentalCycles = generateVectorRepresentation(cycleBasis.getCycles());

        //1
        long cycleCountS = 0;
        cycleCountS++;

        Set<BitSet> Q = new HashSet<>();
        Q.add(fundamentalCycles.getFirst());
        HashSet<BitSet> R = new HashSet<>();
        HashSet<BitSet> RStar = new HashSet<>();

        for (int i = 1; i < getNumberOfIndependentCycles(formula); i++) { //6
            BitSet currentCycle = fundamentalCycles.get(i);

            //2
            for (BitSet T : Q) {
                BitSet copyT = (BitSet) T.clone();
                copyT.xor(currentCycle);
                if (T.intersects(currentCycle)) R.add(copyT);
                else RStar.add(copyT);
            }

            //3
            var iterator = R.iterator();
            while (iterator.hasNext()) {
                BitSet next =  iterator.next();
                if (R.stream().anyMatch(e -> checkSubset(e, next))) {
                    iterator.remove();
                    RStar.add(next);
                }
            }

            //4
            cycleCountS += R.size();
            cycleCountS++;

            //5
            Q.addAll(R);
            Q.addAll(RStar);
            Q.add(currentCycle);
            R.clear();
            RStar.clear();
        }

        return cycleCountS;
    }

    private boolean checkSubset(BitSet subset, BitSet set) {
        if (subset.equals(set)) return false;
        BitSet subCopy = (BitSet) subset.clone();
        subCopy.xor(set);
        subCopy.or(set);
        return subCopy.equals(set);
    }

    private List<BitSet> generateVectorRepresentation(Set<List<DefaultEdge>> fundamentalCycles){
        List<BitSet> list = new ArrayList<>();
        for (List<DefaultEdge> fundamentalCycle : fundamentalCycles) {
            BitSet bitSet = new BitSet(graph.edgeSet().size());
            for (DefaultEdge edge : fundamentalCycle) {
                bitSet.set(edgeIDMap.get(edge));
            }
            list.add(bitSet);
        }
        return list;
    }


    // computes the number of independent cycles
    // i.e. the vertices in each cycle are not a subset of another one
    public long getNumberOfIndependentCycles(FeatureModelFormula formula) {
        initializeGraph(formula);
        if (cycleBasis == null) computeCycleBasis();
        return cycleBasis.getCycles().size();
    }

    private void computeCycleBasis(){
        StackBFSFundamentalCycleBasis<String, DefaultEdge> computer = new StackBFSFundamentalCycleBasis<>(graph);
        cycleBasis = computer.getCycleBasis();
    }

}
