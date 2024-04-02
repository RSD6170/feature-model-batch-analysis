package org.collection.fm.analyses;

import java.nio.file.Path;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.CNF;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class NumberOfUnitClauses implements IFMAnalysis {

    private static final String LABEL = "NumberOfUnitClauses";
    private static final String DESCRIPTION = "Number of unit clauses, dependent on the used CNF transformation.";

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
        CNF cnf = formula.getCNF();
        return String.valueOf(cnf.getClauses().stream().filter(clause -> clause.size() == 1).count());
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
