package org.collection.fm.analyses;

import org.collection.fm.util.AnalysisCacher;
import org.collection.fm.util.FMUtils;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class NumberOfDeadFeatures implements IFMAnalysis {

    private static final String LABEL = "Number_Dead";
    private final AnalysisCacher analysisCacher;

    public NumberOfDeadFeatures(AnalysisCacher analysisCacher) {
        this.analysisCacher = analysisCacher;
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
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout) {
        try {
            return String.valueOf(analysisCacher.getDeadFeatureNumber(formula, timeout));
        } catch (Exception e) {
            System.out.println("NumberOfDeadFeatures just crashed!");
            e.printStackTrace();
            return "?";
        }
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
