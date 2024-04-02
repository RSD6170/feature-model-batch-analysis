package org.collection.fm.analyses;

import java.nio.file.Path;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class NumberOfOrs implements IFMAnalysis {

    private static final String LABEL = "NumberOfOrs";
    private static final String DESCRIPTION = "Number of or groups in the feature model";

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
        return String.valueOf(featureModel.getFeatures().stream().filter(f -> f.getStructure().isOr()).count());
    }

    @Override
    public String getResult(Node node) {
        return null;
    }

    @Override
    public boolean supportsFormat(Format format) {
        return format == Format.FEATURE_MODEL;
    }

}
