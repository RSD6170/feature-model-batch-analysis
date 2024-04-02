package org.collection.fm.analyses;

import java.nio.file.Path;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class NumberOfTwoClauses implements IFMAnalysis {

    private static final String LABEL = "NumberOfTwoClauses";
    private static final String DESCRIPTION = "Number of clauses with exactly two literals (i.e., implications), dependent on CNF transformation";

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
        return String.valueOf(formula.getCNF().getClauses().stream().filter(clause -> clause.size() == 2).count());
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
