package org.collection.fm.analyses;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

import org.collection.fm.formulagraph.ConnectivityGraph;
import org.prop4j.Node;

import java.nio.file.Path;

public class IndependentCyclomaticComplexity implements IFMAnalysis {

    private static final String LABEL = "IndependentCyclomaticComplexity";

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
        return Integer.toString(ConnectivityGraph.getNumberOfIndependentCycles(formula));
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
