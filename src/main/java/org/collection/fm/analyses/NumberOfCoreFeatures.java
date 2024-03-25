package org.collection.fm.analyses;

import org.collection.fm.util.AnalysisCacher;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

import java.nio.file.Path;

public class NumberOfCoreFeatures implements IFMAnalysis  {

    private static final String LABEL = "Number_CORE";
    private final AnalysisCacher analysisCacher;

    public NumberOfCoreFeatures(AnalysisCacher analysisCacher) {
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
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout, Path solverRelativePath) {
        try {
            return String.valueOf(analysisCacher.getCoreFeatureNumber(formula, timeout));
        } catch (Exception e) {
            System.out.println("NumberOfCoreFeatures just crashed!");
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
