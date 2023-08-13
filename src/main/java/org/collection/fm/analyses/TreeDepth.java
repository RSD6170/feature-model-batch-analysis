package org.collection.fm.analyses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;

public class TreeDepth implements IFMAnalysis {

    private static final String LABEL = "TreeDepth";

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
        return Integer.toString(getTreeDepthRecursive(featureModel.getStructure().getRoot()));
    }
    

    private static int getTreeDepthRecursive(IFeatureStructure structure) {
		List<Integer> depthOfChildren = new ArrayList<>();
		if (structure.getChildrenCount() == 0) {
			return 1; // decide whether 0 or 1 is correct
		}
		for(IFeatureStructure child : structure.getChildren()) {
			depthOfChildren.add(getTreeDepthRecursive(child));
		}
		return Collections.max(depthOfChildren) + 1;
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
