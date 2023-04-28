package org.collection.fm.analyses;

import org.prop4j.Node;
import org.prop4j.Or;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class NumberOfLiterals implements IFMAnalysis {

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
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula) {
        Node cnf = formula.getCNFNode();
        int numberOfLiterals = 0;
        for (Node clause : cnf.getChildren()) {
            if (!(clause instanceof Or)) {
                System.out.println("Not CNF");
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
