package org.collection.fm.formulagraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.prop4j.Node;
import org.prop4j.Or;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;

public class ConnectivityGraph {

    private Map<String, Vertex> vertices;

    public ConnectivityGraph(FeatureModelFormula formula) {
        this.vertices = new HashMap<>();
        initializeGraph(formula);
    }

    public Vertex getVertex(String variable) {
        return vertices.get(variable);
    }

    public void addVertex(String variable) {
        vertices.put(variable, new Vertex(variable));
    }

    private void initializeGraph(FeatureModelFormula formula) {
        Node cnf = formula.getCNFNode(); 
        for (Node clause : cnf.getChildren()) {
            handleClause(clause);
        }
    }
    
    private void handleClause(Node set) {
        if (set.getChildren().length < 2) {
            return;
        }
        if (!(set instanceof Or)) {
            System.out.println("Not CNF");
        }
        for (int i = 0; i < set.getChildren().length - 1; i++) {
            for (int j = i+1; j < set.getChildren().length; j++) {
                handlePair((String) set.getChildren()[i].getLiterals().get(0).var, (String) set.getChildren()[j].getLiterals().get(0).var);
            }
        }
    }
    
    private void handlePair(String a, String b) {
        if (getVertex(a) == null) {
            addVertex(a);
        }
        if (getVertex(b) == null) {
            addVertex(b);
        }
        getVertex(a).addNeighbor(b);
        getVertex(b).addNeighbor(a);
    }

    public int getNumberOfEdges() {
        int edges = 0;
        for (Vertex vertex : vertices.values()) {
            edges += vertex.getNumberOfNeighbors();
        }

        return edges / 2;
    }

    // performs a dfs to compute the number of cycles
    public int getNumberOfCycles() {
        int cycleCounter = 0;
        Stack<String> stack = new Stack<>();
        String start =  vertices.values().iterator().next().getVariable();
        stack.push(start);
        while (!stack.isEmpty()) {
            Vertex current = getVertex(stack.pop());
            current.setVisited(true);
            for (String dest : current.getAdjacencyList()) {
                Vertex destV = getVertex(dest);
                if (stack.contains(dest) && !destV.isVisited()) { // cycle found
                    cycleCounter++;
                } else if (!destV.isVisited()) {
                    stack.push(dest);
                }
            }
        }
        return cycleCounter;
    }

    // computes the number of independent cycles
    // i.e. the vertices in each cycle are not a subset of another one
    public int getNumberOfIndependentCycles() {
        Stack<String> stack = new Stack<>();
        String start =  vertices.values().iterator().next().getVariable();
        Vector<Vector<String>> cycleLog = new Vector<>();
        stack.push(start);

        // key: child, value: parent
        HashMap<String, String> parentMapping = new HashMap<>();
        parentMapping.put(start, null);
        while (!stack.isEmpty() && !Thread.currentThread().isInterrupted()) {
            String current = stack.pop();
            Vertex currentV = getVertex(current);
            currentV.setVisited(true);
            for (String dest : currentV.getAdjacencyList()) {
                if (Thread.currentThread().isInterrupted()) return -1;
                parentMapping.put(dest, current);
                Vertex destV = getVertex(dest);
                if (stack.contains(dest) && !destV.isVisited()) { // cycle found
                    // get the path beginning at the root of the dfs
                    Vector<String> pathLog = new Vector<>();
                    pathLog.add(dest);
                    String next = current;
                    while (next != null && !pathLog.contains(next)) {
                        pathLog.add(next);
                        next = parentMapping.get(next);
                    }

                    // check for not independent cycles before inserting
                    boolean independent = true;
                    for(Vector<String> elem :  cycleLog) {
                        if (Thread.currentThread().isInterrupted()) return -1;
                        if (elem.size() >= pathLog.size()) {
                            if (elem.containsAll(pathLog)) {
                                independent = false;
                                break;
                            }
                        } else {
                            if (pathLog.containsAll(elem)) {
                                independent = false;
                                break;
                            }
                        }
                    }

                    if (independent) {cycleLog.add(pathLog);}
                } else if (!destV.isVisited()) {
                    stack.push(dest);
                }
            }
        }
        return cycleLog.size();
    }
}