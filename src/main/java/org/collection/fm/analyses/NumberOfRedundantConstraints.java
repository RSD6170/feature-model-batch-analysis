package org.collection.fm.analyses;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.collection.fm.util.AnalysisCacher;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.CNF;
import de.ovgu.featureide.fm.core.analysis.cnf.analysis.AddRedundancyAnalysis;
import de.ovgu.featureide.fm.core.analysis.cnf.analysis.IndependentRedundancyAnalysis;
import de.ovgu.featureide.fm.core.analysis.cnf.analysis.RemoveRedundancyAnalysis;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.job.LongRunningWrapper;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

public class NumberOfRedundantConstraints implements IFMAnalysis {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String LABEL = NumberOfRedundantConstraints.class.getSimpleName();

    private AnalysisCacher analysisCacher;

    public NumberOfRedundantConstraints(AnalysisCacher analysisCacher) {
        this.analysisCacher = analysisCacher;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return "Number of returned constraints, computed with FeatureIDE, iteratively adds clauses and checks when added redundancy";
    }

    @Override
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout,
            Path solverRelativePath) throws InterruptedException {
        try {
            FeatureModelAnalyzer analyzer = analysisCacher.getAnalyzer(formula);
            return String.valueOf(analyzer.getRedundantConstraints(new NullMonitor<>()).size());
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'supportsFormat'");
    }

}
