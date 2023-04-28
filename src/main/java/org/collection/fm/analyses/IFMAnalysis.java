package org.collection.fm.analyses;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public interface IFMAnalysis {

    enum Format {
        FEATURE_MODEL, CNF
    }

    /**
     * Provide label that is used as CSV column header
     * @return
     */
    public String getLabel();

    /**
     * Provide a description to improve understandability when using the -h option
     * @return description
     */
    public String getDescription();

    /**
     * Computes result of the analyis for a given feature model
     * @param featureModel
     * @return String to be saved in the csv for that model
     */
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula);


    /**
     * 
     * @param node
     * @return
     */
    public String getResult(Node node);

    /**
     * 
     * @param format
     * @return
     */
    public boolean supportsFormat(Format format);

}
