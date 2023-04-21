package de.uulm.fm.analyses;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.uulm.fm.util.FMUtils;

public class NumberOfDeadFeatures implements IFMAnalysis {

    private static final String LABEL = "#Dead";

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula) {
        return String.valueOf(FMUtils.getCoreFeatures(formula).size());
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
