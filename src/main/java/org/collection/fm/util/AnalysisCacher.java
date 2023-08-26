package org.collection.fm.util;

import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.analysis.cnf.analysis.CoreDeadAnalysis;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

import java.util.List;
import java.util.Objects;

public class AnalysisCacher {

    private FeatureModelFormula formula;

    private List<String> coreFeatures;
    private List<String> deadFeatures;

    public int getCoreFeatureNumber(FeatureModelFormula formula, int timeout) throws Exception {
        generateResults(formula, timeout);
        return coreFeatures.size();
    }

    public int getDeadFeatureNumber(FeatureModelFormula formula, int timeout) throws Exception {
        generateResults(formula, timeout);
        return deadFeatures.size();
    }


    private void generateResults(FeatureModelFormula formula, int timeout) throws Exception {
        if (!formula.equals(this.formula) || Objects.isNull(coreFeatures) || Objects.isNull(deadFeatures)){
            this.formula = formula;

            CoreDeadAnalysis coreDeadAnalysis = new CoreDeadAnalysis(formula.getCNF());
            coreDeadAnalysis.setTimeout(1000*timeout);
            LiteralSet result = coreDeadAnalysis.execute(new NullMonitor<>());
            coreFeatures = formula.getCNF().getVariables().convertToString(result, true, false, false);
            deadFeatures = formula.getCNF().getVariables().convertToString(result, false, true, false);
        }
    }
}
