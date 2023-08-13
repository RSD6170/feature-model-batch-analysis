package org.collection.fm.analyses;

import org.collection.fm.formulagraph.ConnectivityGraph;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class ConnectivityDensity implements IFMAnalysis {

    private static final String LABEL = "ConnectivityDensity";

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout) {
        ConnectivityGraph graph = new ConnectivityGraph(formula);
        int numberOfEdges = graph.getNumberOfEdges();
        return Integer.toString(numberOfEdges);
    }

    @Override
    public String getResult(Node node) {
        return null;
    }

    @Override
    public boolean supportsFormat(Format format) {
        return false;
    }
    
}
