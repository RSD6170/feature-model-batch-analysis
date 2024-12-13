package org.collection.fm.analyses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.collection.fm.util.AnalysisCacher;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

import java.nio.file.Path;

public class RatioOfOptionalFeatures implements IFMAnalysis{

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String LABEL = "RatioOptionalFeatures";
    private final AnalysisCacher analysisCacher;

    public RatioOfOptionalFeatures(AnalysisCacher analysisCacher) {
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
            int numberOfFeatures = featureModel.getNumberOfFeatures();
            return Double.toString((double)(numberOfFeatures - analysisCacher.getCoreFeatureNumber(formula, timeout) - analysisCacher.getDeadFeatureNumber(formula, timeout)) / numberOfFeatures);
        } catch (Exception e) {
            LOGGER.warn("{} just crashed", LABEL, e);
            return "?";
        }
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
