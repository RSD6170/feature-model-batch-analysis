package de.uulm.fm.analyses;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class AverageConstraintSize implements IFMAnalysis {

    private static final String LABEL = "AverageConstraintSize";

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
        double sizeCount = 0;
		int numberOfConstraints = featureModel.getConstraintCount();
		if (numberOfConstraints == 0) {
			return "0";
		}
		for (IConstraint constraint : featureModel.getConstraints()) {
			sizeCount += constraint.getNode().getLiterals().size();
		}
		return Double.toString(sizeCount / numberOfConstraints);
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
