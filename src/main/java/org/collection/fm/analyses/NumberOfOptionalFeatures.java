package org.collection.fm.analyses;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.collection.fm.util.AnalysisCacher;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class NumberOfOptionalFeatures implements IFMAnalysis {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String LABEL = "NumberOfOptionalFeatures";
    private static final String DESCRIPTION = "Number of features that are neither core nor dead.";
    private AnalysisCacher analysisCacher;

    public NumberOfOptionalFeatures(AnalysisCacher analysisCacher) {
        this.analysisCacher = analysisCacher;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout,
            Path solverRelativePath) throws InterruptedException {
        try {
            return String.valueOf(featureModel.getFeatures().size() - analysisCacher.getCoreFeatureNumber(formula, timeout) - analysisCacher.getDeadFeatureNumber(formula, timeout));
        } catch (Exception e) {
            LOGGER.warn("{} just crashed", LABEL, e);
            return "?";
        }
    }

    @Override
    public String getResult(Node node) {
        return null;
    }

    @Override
    public boolean supportsFormat(Format format) {
        return true;
    }

}
