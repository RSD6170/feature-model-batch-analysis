package org.collection.fm.analyses;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;

public class NumberOfLeafFeatures implements IFMAnalysis {

    private static final String LABEL = "NumberOfLeafFeatures";

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
        return Integer.toString(getNumberOfLeafChildren(featureModel.getStructure().getRoot()));
    }

    	
	private static int getNumberOfLeafChildren(IFeatureStructure structure) {
		int count = 0;
		if (structure.getChildrenCount() ==  0) {
			return 1;
		}
		for(IFeatureStructure child :structure.getChildren()) {
			count += getNumberOfLeafChildren(child);
		}
		return count;
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
