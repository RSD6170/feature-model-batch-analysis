package org.collection.fm.util;

import org.collection.fm.analyses.*;
import org.collection.fm.handler.FeatureStepHandler;
import org.collection.fm.handler.SatzillaHandler;

public enum AnalysisStepsEnum {

    FeatureBasic,
    FeatureConstraints,
    FeatureTree,
    FeatureClause,
    FeatureDense,
    FeatureCore,
    FeatureValid,
    FeatureCyclo,
    SatzillaBASE,
    SatzillaSP,
    SatzillaDIA,
    SatzillaCL,
    SatzillaUNIT,
    SatzillaLS,
    SatzillaLOBJOIS,
    SatzillaLP;

    public FeatureStepHandler getFeatureStepHandler(int timeout) {
        return switch (this) {
            case FeatureBasic -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, "Feature-basic");
                featureStepHandler.addAnalysis(new NumberOfFeatures());
                featureStepHandler.addAnalysis(new NumberOfLeafFeatures());
                featureStepHandler.addAnalysis(new NumberOfTopFeatures());
                yield featureStepHandler;
            }
            case FeatureConstraints -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, "Feature-constraints");
                featureStepHandler.addAnalysis(new NumberOfConstraints());
                featureStepHandler.addAnalysis(new AverageConstraintSize());
                featureStepHandler.addAnalysis(new CtcDensity());
                featureStepHandler.addAnalysis(new FeaturesInConstraintsDensity());
                yield featureStepHandler;
            }
            case FeatureTree -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, "Feature-tree");
                featureStepHandler.addAnalysis(new TreeDepth());
                featureStepHandler.addAnalysis(new AverageNumberOfChilden());
                yield featureStepHandler;
            }
            case FeatureClause -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, "Feature-clause");
                featureStepHandler.addAnalysis(new NumberOfClauses());
                featureStepHandler.addAnalysis(new NumberOfLiterals());
                featureStepHandler.addAnalysis(new ClauseDensity());
                yield featureStepHandler;
            }
            case FeatureDense -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, "Feature-dense");
                featureStepHandler.addAnalysis(new ConnectivityDensity());
                yield featureStepHandler;
            }
            case FeatureCore -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, "Feature-core");
                featureStepHandler.addAnalysis(new VoidModel());

                AnalysisCacher analysisCacher = new AnalysisCacher();

                featureStepHandler.addAnalysis(new NumberOfCoreFeatures(analysisCacher));
                featureStepHandler.addAnalysis(new NumberOfDeadFeatures(analysisCacher));
                featureStepHandler.addAnalysis(new RatioOfOptionalFeatures(analysisCacher));
                yield featureStepHandler;
            }
            case FeatureValid -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, "Feature-valid");
                featureStepHandler.addAnalysis(new NumberOfValidConfigurationsLog());
                yield featureStepHandler;
            }
            case FeatureCyclo -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, "Feature-cyclo");
                featureStepHandler.addAnalysis(new SimpleCyclomaticComplexity());
                featureStepHandler.addAnalysis(new IndependentCyclomaticComplexity());
                yield featureStepHandler;

            }
            case SatzillaBASE -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.BASE);
            case SatzillaSP -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.SP);
            case SatzillaDIA -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.DIA);
            case SatzillaCL -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.CL);
            case SatzillaUNIT -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.UNIT);
            case SatzillaLS -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.LS);
            case SatzillaLOBJOIS -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.LOBJOIS);
            case SatzillaLP -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.LP);
        };
    }

}
