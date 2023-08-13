package org.collection.fm.analyses;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class AverageNumberOfChilden implements IFMAnalysis {

    private static final String LABEL = "AverageNumberOfChildren";

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
		int childrenCount = 0;
		int numberOfNonLeafFeatures = 0;
		for (IFeature feature : featureModel.getFeatures()) {
			int numberOfChildren = feature.getStructure().getChildrenCount();
			if (numberOfChildren != 0) {
				numberOfNonLeafFeatures++;
			}
			childrenCount += (feature.getStructure().getChildrenCount());
		}
		if (numberOfNonLeafFeatures == 0) {
			return "0";
		}
		return Double.toString((double)childrenCount / numberOfNonLeafFeatures);
    }

	@Override
	public String getResult(Node node) {
		return null;
	}

	@Override
	public boolean supportsFormat(Format format) {
		return format == Format.FEATURE_MODEL;
	}
    
}
