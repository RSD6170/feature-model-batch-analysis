package de.uulm.fm.analyses;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

public class RatioOfOptionalFeatures implements IFMAnalysis{

    private static final String LABEL = "RatioOptionalFeatures";

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
        int numberOfFeatures = featureModel.getNumberOfFeatures();
        int numberOfCoreFeatures = formula.getAnalyzer().getCoreFeatures(new NullMonitor<>()).size();
        int numberOfDeadFeatures = formula.getAnalyzer().getDeadFeatures(new NullMonitor<>()).size();
        return Double.toString((double)(numberOfFeatures - numberOfCoreFeatures - numberOfDeadFeatures) / numberOfFeatures);
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
