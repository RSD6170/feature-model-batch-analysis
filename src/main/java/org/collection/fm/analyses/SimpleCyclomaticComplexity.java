package org.collection.fm.analyses;

import org.collection.fm.formulagraph.ConnectivityGraph;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

import java.nio.file.Path;

public class SimpleCyclomaticComplexity implements IFMAnalysis {

    private static final String LABEL = "SimpleCyclomaticComplexity";
    private final ConnectivityGraph connectivityGraph;

    public SimpleCyclomaticComplexity(ConnectivityGraph connectivityGraph) {
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
        return Long.toString(connectivityGraph.getNumberOfCycles(formula));
    }

    @Override
    public String getResult(Node node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supportsFormat(Format format) {
        // TODO Auto-generated method stub
        return false;
    }
    
}
