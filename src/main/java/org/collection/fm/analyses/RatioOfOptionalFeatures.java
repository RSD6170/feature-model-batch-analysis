package org.collection.fm.analyses;

import de.ovgu.featureide.fm.core.AnalysesCollection;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.analysis.cnf.analysis.CoreDeadAnalysis;
import de.ovgu.featureide.fm.core.analysis.cnf.analysis.HasSolutionAnalysis;
import de.ovgu.featureide.fm.core.functional.Functional;
import org.collection.fm.util.AnalysisCacher;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

import java.util.Collections;

public class RatioOfOptionalFeatures implements IFMAnalysis{

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
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout) {
        try {
            int numberOfFeatures = featureModel.getNumberOfFeatures();
            return Double.toString((double)(numberOfFeatures - analysisCacher.getCoreFeatureNumber(formula, timeout) - analysisCacher.getDeadFeatureNumber(formula, timeout)) / numberOfFeatures);
        } catch (Exception e) {
            System.out.println("RatioOfOptionalFeatures just crashed!");
            e.printStackTrace();
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
