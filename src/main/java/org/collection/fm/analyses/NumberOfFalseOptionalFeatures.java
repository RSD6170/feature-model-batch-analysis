package org.collection.fm.analyses;

import java.nio.file.Path;
import java.util.List;

import org.collection.fm.util.AnalysisCacher;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

public class NumberOfFalseOptionalFeatures implements IFMAnalysis {

    private static final String LABEL = "NumberOfFalseOptionalFeatures";

    private AnalysisCacher analysisCacher;

    public NumberOfFalseOptionalFeatures(AnalysisCacher analysisCacher) {
        this.analysisCacher = analysisCacher;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return "Number of false-optional features. A feature is false-optional if it is labelled as optional but its included is required if its parent is selected.";
    }

    @Override
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout,
        Path solverRelativePath) throws InterruptedException {
        FeatureModelAnalyzer analyzer = analysisCacher.getAnalyzer(formula);
        try {
            List<IFeature> features = analyzer.getFalseOptionalFeatures(new NullMonitor<>());
            System.out.println(features);
            return String.valueOf(features.size());
        } catch (Exception e) {
            System.out.println(LABEL + " just crashed!");
            e.printStackTrace();
            return "?";
        }

    }

    @Override
    public String getResult(Node node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResult'");
    }

    @Override
    public boolean supportsFormat(Format format) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'supportsFormat'");
    }

}
