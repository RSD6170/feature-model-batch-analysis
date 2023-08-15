package org.collection.fm.analyses;

import org.collection.fm.util.FMUtils;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class VoidModel implements IFMAnalysis {

    private static final String LABEL = "Void";
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
        return FMUtils.isVoid(formula) ? "1" : "0";
    }

    @Override
    public String getResult(Node node) {
        return "";
    }

    @Override
    public boolean supportsFormat(Format format) {
        return false;
    }
    
}
