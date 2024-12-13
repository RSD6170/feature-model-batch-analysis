package org.collection.fm.analyses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.collection.fm.util.FMUtils;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

import java.nio.file.Path;

public class VoidModel implements IFMAnalysis {

    private static final Logger LOGGER = LogManager.getLogger();

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
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout, Path solverRelativePath) {
        try {
            return FMUtils.isVoid(formula, timeout) ? "1" : "0";
        } catch (Exception e) {
            LOGGER.warn("{} just crashed", LABEL, e);
            return "?";
        }
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
