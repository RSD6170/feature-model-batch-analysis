package org.collection.fm.analyses;

import org.collection.fm.formulagraph.ConnectivityGraph;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

import java.nio.file.Path;

public class ConnectivityDensity implements IFMAnalysis {

    private static final String LABEL = "ConnectivityDensity";
    private final ConnectivityGraph connectivityGraph;

    public ConnectivityDensity(ConnectivityGraph connectivityGraph) {
        this.connectivityGraph = connectivityGraph;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout, Path solverRelativePath) {
        return Long.toString(connectivityGraph.getNumberOfEdges(formula));
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
