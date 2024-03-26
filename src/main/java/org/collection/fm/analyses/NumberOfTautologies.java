package org.collection.fm.analyses;

import java.nio.file.Path;

import org.collection.fm.util.AnalysisCacher;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

public class NumberOfTautologies implements IFMAnalysis {

    private static final String LABEL = "NumberOfTautologies";

    private AnalysisCacher analysisCacher;

    public NumberOfTautologies(AnalysisCacher analysisCacher) {
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
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout,
            Path solverRelativePath) throws InterruptedException {
        try {
            FeatureModelAnalyzer analyzer = analysisCacher.getAnalyzer(formula);
            return String.valueOf(analyzer.getTautologyConstraints(new NullMonitor<>()).size());
        } catch (Exception e) {
            System.out.println(LABEL + " just crashed!");
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
        return true;
    }

}
