package org.collection.fm.analyses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prop4j.Node;
import org.prop4j.Or;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

import java.nio.file.LinkOption;
import java.nio.file.Path;

public class NumberOfLiterals implements IFMAnalysis {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String LABEL = "NumberOfLiterals";


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
        Node cnf = formula.getCNFNode();
        int numberOfLiterals = 0;
        for (Node clause : cnf.getChildren()) {
            if (Thread.currentThread().isInterrupted()) break;
            if (!(clause instanceof Or)) {
                LOGGER.warn("Not CNF in {}", LABEL);
                return null;
            }
            numberOfLiterals += clause.getLiterals().size();
        }
        return Integer.toString(numberOfLiterals);
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
